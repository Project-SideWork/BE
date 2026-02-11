importScripts('https://www.gstatic.com/firebasejs/10.7.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.7.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "AIzaSyDwHKbyXY2YqiV1dwClKArYz8NqOLPJH3o",
  authDomain: "fcmtest-2eeed.firebaseapp.com",
  projectId: "fcmtest-2eeed",
  storageBucket: "fcmtest-2eeed.firebasestorage.app",
  messagingSenderId: "903263294981",
  appId: "1:903263294981:web:c7c45f4fdd279a17deee6c"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  const title = payload.notification?.title ?? payload.data?.title ?? '알림';
  const options = {
    body: payload.notification?.body ?? payload.data?.body ?? '',
    icon: payload.data?.icon ?? undefined
  };
  return self.registration.showNotification(title, options);
});
