
/*
===============================
                    The marqur Backend
===============================       
*/


// Required SDKs
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const express = require('express');
const cors = require('cors');
const ngeohash = require('ngeohash');

// If deployed locally,
if (isThisLocalhost) {

    //  Initialize app with service account
    var serviceAccount = require("./service-permit.json");
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: "https://marqur.firebaseio.com"
    });
    console.log("Using Local Mode!"); 

} 

// Else, initialize app with default configuration
else admin.initializeApp(functions.config().firebase);


const db = admin.firestore();

// Initialize Express app
const app = express();

// Allow Cross-Origin stuff
app.use(cors({ origin: true })); 


// Add-Marker request
app.post('/api/create', (req, res) => {
  (async () => {
      try {
          var author = req.body.author;
          var location = new admin.firestore.GeoPoint(req.body.location.x,req.body.location.y);
          var geohash = ngeohash.encode(req.body.location.x,req.body.location.y)
          var content = req.body.content
          var marker = {
            author: author,
            location: location,
            geohash: geohash,
            content: content,
            date_created: admin.firestore.FieldValue.serverTimestamp(),
            date_modified: admin.firestore.FieldValue.serverTimestamp(),
            views:0,
            upvotes: 0,
            downvotes: 0,
            comments_count: 0,
            reports: 0
          };
          await db.collection('markers')
              .add(marker)
              .then(function(docRef) {
                docRef.update({marker_id:docRef.id});
            });
          return res.status(200).send();
      } catch (error) {
          console.log(error);
          return res.status(500).send(error);
      }
    })();
});


// Read-Marker Request
app.get('/api/read/:marker_id', (req, res) => {
  (async () => {
      try {
          const document = db.collection('markers').doc(req.params.marker_id);
          let marker = await document.get();
          let response = marker.data();
          return res.status(200).send(response);
      } catch (error) {
          console.log(error);
          return res.status(500).send(error);
      }
      })();
  });


// Update-Marker Request
app.put('/api/update/:marker_id', (req, res) => {
  (async () => {
      try {
          const document = db.collection('markers').doc(req.params.marker_id);
          await document.update({
              content: req.body
          });
          return res.status(200).send();
      } catch (error) {
          console.log(error);
          return res.status(500).send(error);
      }
      })();
  });
  
  // Delete-Marker Request
  app.delete('/api/delete/:marker_id', (req, res) => {
  (async () => {
      try {
          const document = db.collection('markers').doc(req.params.marker_id);
          await document.delete();
          return res.status(200).send();
      } catch (error) {
          console.log(error);
          return res.status(500).send(error);
      }
      })();
  });











// Export the app
exports.app = functions.https.onRequest(app);




/**
 * Returns whether app is running locally
 * @param {Request} req - The HTTP request object
 */
var isThisLocalhost = function (req){
    
    var ip = req.connection.remoteAddress;
    var host = req.get('host');    
    return ip === "127.0.0.1" || ip === "::ffff:127.0.0.1" || ip === "::1" || host.indexOf("localhost") !== -1;
}


/**
 * Triggers on new user creation (via Firebase Authentication)
 */
exports.addUserToFirestore = functions.auth.user().onCreate( async (user) => {

    // console.log('New User -> Adding to Firestore... ');

	await admin.firestore().collection( 'users' ).doc(user.uid)
	.set({

		userid: user.uid,
		username: null,
		email : user.email,
		date : user.metadata.creationTime,
		upvotes: 0,
		downvotes: 0,
		location: null,
		markers: null

	})

	.then(writeResult => {

        // console.log('Result: ', writeResult);
		// console.log('Added user '+user.uid+' (email: '+user.email+' ,date: '+user.metadata.createdAt+' to FireStore)');
		return;
		
	})

	.catch(err => {

       console.log(err);
	   return;
	   
	});

	
});



/** 
 * Triggers on user deletion
 */
exports.removeUserFromFirestore = functions.auth.user().onDelete( async (user) => {

	// console.log('User Deleted -> Removing from Firestore... ');
	
	await admin.firestore().collection( 'users' ).doc(user.uid)
	.delete()

	.then(writeResult => {

		// console.log('Result: ', writeResult);
		// console.log('Removed user'+user.uid+' (email: '+user.email+' ,date: '+user.metadata.createdAt+' ) from FireStore');
		return;
		
	})
	
	.catch(err => {

       console.log(err);
	   return;
	   
	});;


});



/**
 * Initiate a recursive delete of documents at a given path.
 * 
 * The calling user must be authenticated and have the custom "admin" attribute
 * set to true on the auth token.
 * 
 * This delete is NOT an atomic operation and it's possible
 * that it may fail after only deleting some documents.
 * 
 * @param {string} data.path the document or collection path to delete.
 */
// exports.recursiveDelete = functions
//   .runWith({

//     timeoutSeconds: 540,
//     memory: '2GB'

//   })
//   .https.onCall((data, context) => {

//     // Only allow admin users to execute this function.
//     if (!(context.auth && context.auth.token && context.auth.token.admin)) {
//       throw new functions.https.HttpsError(
//         'permission-denied',
//         'Must be an administrative user to initiate delete.'
//       );
//     }

//     const path = data.path;
//     console.log(
//       `User ${context.auth.uid} has requested to delete path ${path}`
//     );

//     // Run a recursive delete on the given document or collection path.
//     // The 'token' must be set in the functions config, and can be generated
//     // at the command line by running 'firebase login:ci'.
//     return firebase_tools.firestore
//       .delete(path, {

//         project: process.env.GCLOUD_PROJECT,
//         recursive: true,
//         yes: true,
//         token: functions.config().fb.token

//       })
//       .then(() => {

//         return {
//           path: path 
//         };

//       });
//   });index.js

