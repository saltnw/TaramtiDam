var distance = require("google-distance");
var moment = require("moment")

distance.apiKey = "AIzaSyAYeIfHqg3oAgUqQgJT0Xz2ojY1moqLAXg";

const nodemailer = require('nodemailer');

// create reusable transporter object using the default SMTP transport
var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'saltnworkshop@gmail.com',
        pass: 'a1a2a3a4'
    }
});

function sendNotifications(ref, mdaRef)
{
   console.log("called send notifications (daily email notifications)");
    var tommorowRef = mdaRef.child("Tommorow")
    var mobilesDetailsArr = [];
    var mobilesLocArr = [];
    var mobilesFilled = false;
    tommorowRef.once("value")
      .then(function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var location = childSnapshot.val()["latitude"] + ", " + childSnapshot.val()["longitude"];
          var startTime = childSnapshot.val()["start time"]
          var endTime = childSnapshot.val()["end time"];
          var address = childSnapshot.val()["address"] + ", " + childSnapshot.val()["city"] ;
          var description = childSnapshot.val()["description"];
          var detailsRow = {location, startTime, endTime, address, description};
          mobilesLocArr.push(location);
          mobilesDetailsArr.push(detailsRow);
        }
      );
      mobilesFilled = true;
    });

    while (!mobilesFilled);

    if (mobilesLocArr.length == 0)
      return;

    // Loop through users in order with the forEach() method. The callback
    // provided to forEach() will be called synchronously with a DataSnapshot
    // for each child:
    var usersRef = ref.child("users");
    usersRef.once("value")
      .then(function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var sendMails = childSnapshot.val()["sendMails"];
          if (sendMails != null)
          {
            if (!sendMails)
            {
              //console.log("send mails is false for " + childSnapshot.val()["fullName"]);
              return;
            }
          }
          var lastDonationStr = childSnapshot.val()["lastDonationInString"];
          var isInLast3Months = checkLastDonationDate(lastDonationStr);
          if (isInLast3Months)
          {
            //console.log(childSnapshot.val()["fullName"]+ " has donated in the last 3 months");
            return;
          }
          var addressArray = [];
          var email = childSnapshot.val()["email"];
          if (email.trim().length == 0 )
          {
            return;
          }
          var address1 = childSnapshot.val()["address1"];
          var address2 = childSnapshot.val()["address2"];
          if (address1.trim().length != 0)
          {
            addressArray.push(address1);
          }
          if (address2.trim().length != 0)
          {
            addressArray.push(address2);
          }
          if (addressArray.length == 0)
          {
            return;
          }
          for (i =0; i<addressArray.length; i++)
          {
              distance.get(
                {
                  origin: addressArray[i],
                  destinations: mobilesLocArr,
                  mode: 'walking',
                  units: 'metric'
                },
                function(err, data) {
                  if (err) return console.log(err);
                  for (i = 0; i< data.length; i++)
                  {
                    if (Number(data[i]["distance"].split(" ")[0]) < 5) //of the distance is less then five km
                    {
                      sendEmail(email, mobilesDetailsArr[i]);
                    }
                  }
                  //sendTestEmail(email, mobilesDetailsArr[0]);
          });

        }

      });
    });
}

function sendEmail(email, mobileDetails)
{
  console.log(email);
  // setup email data with unicode symbols
  var msgStr = "ניידת התרמת דם תגיע מחר לאיזורך לכתובת" + " " + mobileDetails["address"]  + " בין השעות," + " " + mobileDetails["startTime"]  + " ל-" + mobileDetails["endTime"]
  var htmlStr = "<b>" + msgStr + "</b>"
  let mailOptions = {
      from: '"TaramtiDam" <TaramtiDam@gmail.com>', // sender address
      to: email, // list of receivers
      subject: "ניידת התרמת דם באיזורך מחר", // Subject line
      html: htmlStr // html body
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
        return console.log(error);
    }
    console.log('Message %s sent: %s', info.messageId, info.response);
});
}

function sendTestEmail(email, mobileDetails)
{
  var msgStr = "ניידת להתרמת דם באיזורך מחר בכתובת" + " " + mobileDetails["address"]  + " בין השעות" + " " + mobileDetails["startTime"]  + " ל-" + mobileDetails["endTime"]
  var htmlStr = "<b>" + msgStr + "</b>"
  // setup email data with unicode symbols
  let mailOptions = {
      from: '"TaramtiDam" <TaramtiDam@gmail.com>', // sender address
      to: "shoshan.z@gmail.com", // list of receivers
      subject: detailsStr, // Subject line
      text: 'Hello world ?', // plain text body
      html: htmlStr // html body
  };
  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
        return console.log(error);
    }
    console.log('Message %s sent: %s', info.messageId, info.response);
});
}

function checkLastDonationDate(lastDonationStr)
{
  if (lastDonationStr == "")
  {
    return false;
  }
  var today = moment();

  var lastDonation = moment(lastDonationStr, "DD-MM-YYYY")
  if (lastDonation.add(3, "months").isAfter(today)) //isInLast3Months
  {
    return true;
  }
  return false; //is not inlast3Months
}


module.exports.sendNotifications = sendNotifications;
module.exports.checkLastDonationDate = checkLastDonationDate;
