var functions = require('firebase-functions');
var getMdaPage = require('./getMdaPage.js')
var processMDAPage = require('./processMDAPage.js');
var sendGameNotifications = require('./gameNotifications.js')
var sendWinnerandEraseGameDB = require('./sendWinnerandEraseGameDB.js')

exports.getMdaMobilesDB =
  functions.pubsub.topic('daily-tick').onPublish((event) => {
      return getMdaPage().then(function(page) { return processMDAPage(page); }).then(function() { console.log("Done Executing!")});
    })

exports.gameNotifications =
  functions.pubsub.topic('weekly-tick').onPublish((event) => {
      sendGameNotifications();
    })

exports.resetGame =
  functions.pubsub.topic('three-months-tick').onPublish((event) => {
      sendWinnerandEraseGameDB();
    })