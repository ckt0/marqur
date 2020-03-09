
// marqur | Firebase > Cloud Functions


// Required SDKs
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize admin access to Firebase resources
admin.initializeApp(functions.config().firebase);



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
exports.recursiveDelete = functions
  .runWith({

    timeoutSeconds: 540,
    memory: '2GB'

  })
  .https.onCall((data, context) => {

    // Only allow admin users to execute this function.
    if (!(context.auth && context.auth.token && context.auth.token.admin)) {
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an administrative user to initiate delete.'
      );
    }

    const path = data.path;
    console.log(
      `User ${context.auth.uid} has requested to delete path ${path}`
    );

    // Run a recursive delete on the given document or collection path.
    // The 'token' must be set in the functions config, and can be generated
    // at the command line by running 'firebase login:ci'.
    return firebase_tools.firestore
      .delete(path, {

        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token

      })
      .then(() => {

        return {
          path: path 
        };

      });
  });index.js

