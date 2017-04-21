//in order to get the location coordinantes
var NodeGeocoder = require('node-geocoder');
var options = {
provider: 'google',
};
var geocoder = NodeGeocoder(options);


function onDonationPageReceived(page)
{
  //for the databse push
  var functions = require('firebase-functions');
  var admin = require("firebase-admin");
  admin.initializeApp(functions.config().firebase);
  var db = admin.database();
  var ref = db.ref();
  var nodeRef = ref.child("MDA");

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
    getGeoLocationAndPush(row, nodeRef, i);
});

}

function getGeoLocationAndPush(row, dbRef, timeout)
{
  var fullAdress = row["address"] + " " + row["city"];
  if (!fullAdress.trim()) return;
  setTimeout(function() {
    geocoder.geocode(fullAdress, function(err, res) {
      if (res)
      {
        row["latitude"] = res[0].latitude;
        row["longitude"] = res[0].longitude;
        var newMobileRef =  dbRef.push(row);
        //var geoFire = new GeoFire(newMobileRef)
        // Create a GeoFire index
      }
      if (err)
      {
        console.log(fullAdress);
        console.log(err);
      }
    });
  }, (timeout+1)*1000)
}
module.exports = onDonationPageReceived;
//todo:
//1. go over the array of "rows" with the async library
//2. for each object - call the geo decoder function and save it to the database
//3. save the key from each push and and use
/*
geoFire.set("some_key", [37.79, -122.41]).then(function() {
  console.log("Provided key has been added to GeoFire");
}, function(error) {
  console.log("Error: " + error);
});

*/
