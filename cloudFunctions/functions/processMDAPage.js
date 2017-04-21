//in order to get the location coordinantes
var NodeGeocoder = require('node-geocoder');
var options = {
provider: 'google',
};
var geocoder = NodeGeocoder(options);

//for the databse push
var functions = require('firebase-functions');
var admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
var db = admin.database();
var ref = db.ref();
ref.child("MDA").remove();
var nodeRef = ref.child("MDA");

function onDonationPageReceived(page)
{
  return new Promise((resolve, reject) => {
    var promiseArray = [];
    var cheerio = require('cheerio');
   $ = cheerio.load(page)
   var data = new Array();
   $('tr').each(function(i, tr){
     var children = $(this).children();
     var dateItem = children.eq(0);
     var cityItem = children.eq(1);
     var addressItem = children.eq(3);
     var descriptionItem = children.eq(2);
     //var secondAddressItem = children.eq(4).children().eq(0)[0];
     //console.log("AAAAAAAAAAAAAAAAAAAAAAa");
     //console.log(secondAddressItem)
     var startTimeItem = children.eq(5);
     var endTimeItem = children.eq(6);
     if (i==0 || !dateItem.text().trim()) return;
     var row = {
          "date": dateItem.text(),
         "city": cityItem.text(),
          "address": addressItem.text(),
         "description": descriptionItem.text(),
         //"second address": secondAddressItem.title,
         "start time": startTimeItem.text(),
         "end time": endTimeItem.text(),
      };
      //data.push(row);
      var currPromise = getGeoLocationAndPush(row, i);
      promiseArray.push(currPromise);
    });
    return Promise.all(promiseArray);
  } )
}

function getGeoLocationAndPush(row, timeout)
{
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
          var newMobileRef =  nodeRef.push(row)
          resolve("done");
          var geoFire = new GeoFire(newMobileRef)
          //geoFire.set("geoLoc")                              // Create a GeoFire index
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
