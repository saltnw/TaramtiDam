var functions = require('firebase-functions');
var getMdaPage = require('./getMdaPage.js')
var processMDAPage = require('./processMDAPage.js');

exports.getMdaMobiles =
  functions.pubsub.topic('daily-tick').onPublish((event) => {
      getMdaPage().then(processMDAPage);
    }

  })
