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

function onDonationPageReceived(page)
{
  var ref = db.ref();
  ref.child("MDA").remove();
  var mdaRef = ref.child("MDA");
  return new Promise((resolve, reject) => {
    var promiseArray = [];
    var cheerio = require('cheerio');
   $ = cheerio.load(page)
   $('tr').each(function(i, tr){
     var children = $(this).children();
     var dateItem = children.eq(0);
     var cityItem = children.eq(1);
     var addressItem = children.eq(2); //flipped desc and address 24.06
     var descriptionItem = children.eq(3);
     var startTimeItem = children.eq(5);
     var endTimeItem = children.eq(6);
     if (i==0 || !dateItem.text().trim()) return;
     var row = {
          "date": dateItem.text(),
         "city": cityItem.text(),
          "address": addressItem.text(),
         "description": descriptionItem.text(),
         //"information": informationItem.text(),
         "start time": startTimeItem.text(),
         "end time": endTimeItem.text(),
      };
      var currPromise = getGeoLocationAndPush(row, i, mdaRef);
      promiseArray.push(currPromise);
    });
    return Promise.all(promiseArray).then(() => sendNotifications.sendNotifications(ref, mdaRef));
  } )
}

function getGeoLocationAndPush(row, timeout, mdaRef)
{
  var todayRef = mdaRef.child("Today")
  var tommorowRef = mdaRef.child("Tommorow")
  return new Promise((resolve, reject) => {
    var address = row["address"];
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
          if (moment(row["date"], "DD-MM-YYYY").isSame(today, "day"))
          {
            newMobileRef =  todayRef.push(row);
          }
          else if (moment(row["date"], "DD-MM-YYYY").isSame(tommorow, "day"))
          {
            newMobileRef =  tommorowRef.push(row);
          }
          else //we don't want future dates in the db
          {
              resolve("done");
              return;
          }
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
module.exports = onDonationPageReceived;
