var functions = require('firebase-functions');

function onDonationPageReceived(page)
{
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
   var addressItem = children.eq(2);
   var nameItem = children.eq(3);
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
       "name": nameItem.text(),
       //"second address": secondAddressItem.title,
       "start time": startTimeItem.text(),
       "end time": endTimeItem.text()
    };
    data.push(row);
    nodeRef.push(row);
    //console.log(row);
});

}

module.exports = onDonationPageReceived;
