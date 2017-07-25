//in order to get the location coordinantes
var NodeGeocoder = require("node-geocoder");
var options = {
provider: "google",
 apiKey: "AIzaSyBOlPRJ_fid7C11EUcwxSggWn3lprjcOP0"
};
var geocoder = NodeGeocoder(options);
var moment = require("moment")
//for the databse push
var functions = require('firebase-functions');
var admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
var db = admin.database();
var GeoFire = require("geofire");
var today = moment();
var tommorow = moment().add("1", "days");
var sendNotifications = require("./sendNotifications.js"); 


function processPages(pages)
{
  if (pages.length != 2)
  {
    console.log("getMDAPAge did not return the two wanted pages");
  }
  todayPage = pages[0];
  tommorowPage = pages[1];
  return Promise.all([onDonationPageReceived(todayPage, 0), onDonationPageReceived(tommorowPage, 1)]);
}

function onDonationPageReceived(page, dayIndex)
{
  var ref = db.ref();
  ref.child("MDA").remove();
  var mdaRef = ref.child("MDA");
  var innerRef = null; 
  if (dayIndex == 0)
  {
    innerRef = mdaRef.child("Today");
  }
  else if (dayIndex == 1)
  {
    innerRef = mdaRef.child("Tommorow");
  }
  return new Promise((resolve, reject) => {
    var promiseArray = [];
   var cheerio = require('cheerio');
   $ = cheerio.load(page) 
   var numOfStations = $('div[class="list-group-item"]').length;
   var date = $('input[id="tbDate"]').attr("value");
   for (i =0; i< numOfStations; ++i)
   {
       var address = ($('a[id="rptDonations_lblCity_' + i + '"]').text());
       var startTime = $('span[id="rptDonations_lblBeginTime_' + i + '"]').text();
       var endTime = $('span[id="rptDonations_lblEndTime_' + i + '"]').text();
       var mapStr =  $('a[id="rptDonations_lblCity_' + i + '"]').attr("href").replace("http://maps.google.com?q=", "");
       var row = {
         "date": date,
         "city": "",
         "address": address,
         "description": "",
         "start time": startTime,
         "end time": endTime,
         "map str": mapStr,
      };
      //console.log(row);
      var currPromise = getGeoLocationAndPush(row, i, innerRef);
      promiseArray.push(currPromise);
    };
    return Promise.all(promiseArray).then(() => sendNotifications.sendNotifications(ref, mdaRef));
  } )
}

function getGeoLocationAndPush(row, timeout, mdaRef)
{
  return new Promise((resolve, reject) => {
    var address = row["map str"]; //get the string MDA webstie use for google map and not the full string! 
                                  //the full string includes description as well
    if (address.trim().length == 0)
    {
      address = row["description"];
    }
    var fullAddress = address + " " + row["city"]; 
    fullAddress = fullAddress.trim();
    if (fullAddress.length == 0)
      {
        resolve("done");
        return;
      }
    setTimeout(function() {
      geocoder.geocode(fullAddress, function(err, res) {
        if (res)
        {
          if (res.length == 0)
          {
              resolve("done");
              return;
          }
          row["latitude"] = res[0].latitude;
          row["longitude"] = res[0].longitude;
          var newMobileRef = null;
          newMobileRef =  mdaRef.push(row);
          var geoFire = new GeoFire(newMobileRef);
          geoFire.set("geoLoc", [res[0].latitude, res[0].longitude]).then(function() {
              resolve("done");
          })
        }
        else if (err)
        {
          console.log(fullAddress);
          console.log(err);
          reject("rejected");
        }
        else
          resolve("done");
      });
    }, (timeout+1)*500)
  });
}

//for testing:
//var getMdaPage = require("./getMdaPage.js");
//getMdaPage().then(onDonationPageReceived).then(() => console.log("done!"));
module.exports = processPages;
