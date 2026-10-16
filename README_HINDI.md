# Kandera Notebook — Android प्रोजेक्ट

यह Android प्रोजेक्ट आपकी Kandera Notebook ऐप के लिए है। इसमें नोटबुक एडिटर, File/Edit/Font/Print विकल्प, Android फ़ाइल चयन और प्रिंट सुविधा शामिल हैं।

## केवल मोबाइल से APK बनाना (GitHub Actions)

इस ZIP में GitHub Actions की build फ़ाइल पहले से शामिल है। GitHub आपके लिए ऑनलाइन APK बनाएगा; मोबाइल में Android Studio इंस्टॉल करने की ज़रूरत नहीं है।

1. अपने मोबाइल ब्राउज़र में https://github.com खोलें और साइन इन/नया अकाउंट बनाएँ। अपना पासवर्ड किसी के साथ साझा न करें।
2. **New repository** चुनें और `KanderaNotebook` नाम की repository बनाएँ। शुरुआत में Private चुन सकते हैं।
3. इस ZIP को मोबाइल में Extract करें। ZIP के अंदर मौजूद सभी फ़ाइलें और फ़ोल्डर, खासकर छिपा हुआ `.github` फ़ोल्डर, repository के root में अपलोड करें। फ़ोल्डर संरचना वैसी ही रहनी चाहिए—`app`, `gradle.properties`, `settings.gradle.kts` आदि सीधे root में हों।
4. फ़ाइलें `main` या `master` branch में commit/upload करें। Push होने पर build अपने-आप भी शुरू हो सकता है।
5. Repository में **Actions** टैब खोलें। **Build Kandera Notebook APK** workflow चुनें और ज़रूरत हो तो **Run workflow** दबाएँ।
6. Build पूरा होने तक प्रतीक्षा करें। सफल होने पर उस run को खोलें और नीचे **Artifacts** में `KanderaNotebook-debug-apk` डाउनलोड करें।
7. ZIP artifact को Extract करें; उसमें `app-debug.apk` होगी। उसे अपने Android फोन में खोलकर इंस्टॉल करें। Android पूछे तो केवल तभी “Install unknown apps” की अनुमति दें जब आपने APK अपने ही प्रोजेक्ट के सफल build से प्राप्त की हो।

## ध्यान दें

- इस ZIP में source project और online build workflow है; पहले से बनी APK शामिल नहीं है।
- GitHub पर repository में `.github/workflows/build-apk.yml` फ़ाइल मौजूद होनी चाहिए, तभी workflow चलेगा।
- अगर build लाल ❌ दिखाए, तो Actions run के logs में error देखें और उसे ठीक करना होगा।
- यह debug APK है, Play Store पर प्रकाशित करने के लिए तैयार signed release build नहीं।
- फ़ाइलें खोलने/सेव करने के लिए ऐप Android के सिस्टम file picker का उपयोग करती है। Print Android के सिस्टम Print संवाद से होता है; उपलब्ध होने पर PDF में सेव कर सकते हैं।
