package com.example.localization

enum class AppLanguage(val displayName: String, val code: String, val isRtl: Boolean) {
  ARABIC("العربية", "ar", isRtl = true),
  ENGLISH("English", "en", isRtl = false),
  FRENCH("Français", "fr", isRtl = false)
}

object AppStrings {
  // Brand & Global Header
  fun appTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "خاطف"
    AppLanguage.ENGLISH -> "Khatif"
    AppLanguage.FRENCH -> "Khatif"
  }

  fun appSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحميل الفيديوهات بنقرة واحدة بأعلى جودة"
    AppLanguage.ENGLISH -> "Download videos with one tap at maximum quality"
    AppLanguage.FRENCH -> "Téléchargez des vidéos en un clic en haute qualité"
  }

  fun ultraFastBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "فائق السرعة"
    AppLanguage.ENGLISH -> "Ultra Fast"
    AppLanguage.FRENCH -> "Ultra Rapide"
  }

  // Navigation Tabs
  fun homeTab(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الرئيسية"
    AppLanguage.ENGLISH -> "Home"
    AppLanguage.FRENCH -> "Accueil"
  }

  fun historyTab(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "التحميلات"
    AppLanguage.ENGLISH -> "Downloads"
    AppLanguage.FRENCH -> "Historique"
  }

  fun settingsTab(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الإعدادات"
    AppLanguage.ENGLISH -> "Settings"
    AppLanguage.FRENCH -> "Paramètres"
  }

  // Home Screen: Input & Actions
  fun pastePlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الصق رابط الفيديو هنا مباشرة..."
    AppLanguage.ENGLISH -> "Paste video link directly here..."
    AppLanguage.FRENCH -> "Collez le lien de la vidéo ici..."
  }

  fun pasteButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "لصق"
    AppLanguage.ENGLISH -> "Paste"
    AppLanguage.FRENCH -> "Coller"
  }

  fun clearButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مسح"
    AppLanguage.ENGLISH -> "Clear"
    AppLanguage.FRENCH -> "Effacer"
  }

  fun snatchButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحميل الفيديو الآن"
    AppLanguage.ENGLISH -> "Download Video Now"
    AppLanguage.FRENCH -> "Télécharger la vidéo"
  }

  fun analyzingText(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاري تحليل واستخراج الفيديو..."
    AppLanguage.ENGLISH -> "Analyzing and extracting video..."
    AppLanguage.FRENCH -> "Analyse et extraction en cours..."
  }

  fun previewTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "معاينة الفيديو المستخرج"
    AppLanguage.ENGLISH -> "Extracted Video Preview"
    AppLanguage.FRENCH -> "Aperçu de la vidéo extraite"
  }

  fun selectQuality(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اختر جودة التحميل:"
    AppLanguage.ENGLISH -> "Select Download Quality:"
    AppLanguage.FRENCH -> "Sélectionnez la qualité :"
  }

  fun downloadNow(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحميل الآن"
    AppLanguage.ENGLISH -> "Download Now"
    AppLanguage.FRENCH -> "Télécharger maintenant"
  }

  fun downloading(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاري التحميل والحفظ..."
    AppLanguage.ENGLISH -> "Downloading & saving..."
    AppLanguage.FRENCH -> "Téléchargement en cours..."
  }

  fun snatchAnother(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحميل فيديو آخر"
    AppLanguage.ENGLISH -> "Download Another Video"
    AppLanguage.FRENCH -> "Télécharger une autre vidéo"
  }

  fun readyToSnatchTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاهز لتحميل أي فيديو"
    AppLanguage.ENGLISH -> "Ready to download any video"
    AppLanguage.FRENCH -> "Prêt à télécharger n'importe quelle vidéo"
  }

  fun readyToSnatchDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "انسخ الرابط من أي منصة، ثم الصقه بالأعلى واضغط على 'تحميل الفيديو الآن' للحفظ بدقة عالية."
    AppLanguage.ENGLISH -> "Copy link from any platform, paste above and tap 'Download Video Now' to save in HD."
    AppLanguage.FRENCH -> "Copiez le lien depuis une plateforme, collez-le ci-dessus et appuyez sur 'Télécharger la vidéo'."
  }

  fun viewsCountLabel(lang: AppLanguage, count: String) = when (lang) {
    AppLanguage.ARABIC -> "$count مشاهدة"
    AppLanguage.ENGLISH -> "$count views"
    AppLanguage.FRENCH -> "$count vues"
  }

  fun sizeLabel(lang: AppLanguage, size: String) = when (lang) {
    AppLanguage.ARABIC -> "الحجم: $size"
    AppLanguage.ENGLISH -> "Size: $size"
    AppLanguage.FRENCH -> "Taille : $size"
  }

  // History Screen
  fun historyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "سجل التنزيلات"
    AppLanguage.ENGLISH -> "Downloads History"
    AppLanguage.FRENCH -> "Historique des téléchargements"
  }

  fun totalDownloadsLabel(lang: AppLanguage, count: Int) = when (lang) {
    AppLanguage.ARABIC -> "إجمالي التنزيلات: $count ملفات"
    AppLanguage.ENGLISH -> "Total Downloads: $count files"
    AppLanguage.FRENCH -> "Total des téléchargements : $count fichiers"
  }

  fun clearAllButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مسح الكل"
    AppLanguage.ENGLISH -> "Clear All"
    AppLanguage.FRENCH -> "Tout effacer"
  }

  fun historyEmptyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "السجل فارغ"
    AppLanguage.ENGLISH -> "History is Empty"
    AppLanguage.FRENCH -> "L'historique est vide"
  }

  fun historyEmptyDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "لا توجد فيديوهات محملة حتى الآن. توجه للرئيسية وحمّل أول فيديو!"
    AppLanguage.ENGLISH -> "No downloaded videos yet. Head to Home and download your first video!"
    AppLanguage.FRENCH -> "Aucune vidéo téléchargée pour le moment. Allez à l'accueil et téléchargez votre première vidéo !"
  }

  fun startDownloadingNow(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "ابدأ بالتحميل الآن"
    AppLanguage.ENGLISH -> "Go to Home to Download"
    AppLanguage.FRENCH -> "Aller à l'accueil"
  }

  fun filterAll(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الكل"
    AppLanguage.ENGLISH -> "All"
    AppLanguage.FRENCH -> "Tout"
  }

  fun filterVideo(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "فيديو"
    AppLanguage.ENGLISH -> "Videos"
    AppLanguage.FRENCH -> "Vidéos"
  }

  fun filterAudio(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "صوت فقط"
    AppLanguage.ENGLISH -> "Audio"
    AppLanguage.FRENCH -> "Audio"
  }

  fun clearConfirmTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مسح جميع التنزيلات"
    AppLanguage.ENGLISH -> "Clear All Downloads"
    AppLanguage.FRENCH -> "Effacer tous les téléchargements"
  }

  fun clearConfirmMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "هل أنت متأكد من رغبتك في مسح كافة العناصر في سجل التحميل؟ لا يمكن التراجع عن هذا الإجراء."
    AppLanguage.ENGLISH -> "Are you sure you want to clear all items in your download history? This action cannot be undone."
    AppLanguage.FRENCH -> "Êtes-vous sûr de vouloir effacer tout l'historique ? Cette action est irréversible."
  }

  fun deleteItemTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حذف من السجل"
    AppLanguage.ENGLISH -> "Delete from History"
    AppLanguage.FRENCH -> "Supprimer de l'historique"
  }

  fun deleteItemMessage(lang: AppLanguage, title: String) = when (lang) {
    AppLanguage.ARABIC -> "هل تريد حذف \"$title\" من سجل التنزيلات؟"
    AppLanguage.ENGLISH -> "Do you want to delete \"$title\" from downloads history?"
    AppLanguage.FRENCH -> "Voulez-vous supprimer \"$title\" de l'historique ?"
  }

  fun playSimulatorTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "معاينة التشغيل"
    AppLanguage.ENGLISH -> "Playback Preview"
    AppLanguage.FRENCH -> "Aperçu de lecture"
  }

  fun playSimulatorDesc(lang: AppLanguage, quality: String, size: String, platform: String) = when (lang) {
    AppLanguage.ARABIC -> "مشغل محاكاة يعرض جودة $quality بحجم $size من $platform."
    AppLanguage.ENGLISH -> "Simulation player displaying $quality ($size) from $platform."
    AppLanguage.FRENCH -> "Lecteur de simulation affichant $quality ($size) depuis $platform."
  }

  fun deleteButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حذف"
    AppLanguage.ENGLISH -> "Delete"
    AppLanguage.FRENCH -> "Supprimer"
  }

  fun cancelButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إلغاء"
    AppLanguage.ENGLISH -> "Cancel"
    AppLanguage.FRENCH -> "Annuler"
  }

  fun closeButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إغلاق"
    AppLanguage.ENGLISH -> "Close"
    AppLanguage.FRENCH -> "Fermer"
  }

  fun playButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تشغيل"
    AppLanguage.ENGLISH -> "Play"
    AppLanguage.FRENCH -> "Lire"
  }

  fun pauseButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إيقاف مؤقت"
    AppLanguage.ENGLISH -> "Pause"
    AppLanguage.FRENCH -> "Pause"
  }

  fun shareButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مشاركة"
    AppLanguage.ENGLISH -> "Share"
    AppLanguage.FRENCH -> "Partager"
  }

  fun publishToGallery(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نشر في المعرض"
    AppLanguage.ENGLISH -> "Save to Gallery"
    AppLanguage.FRENCH -> "Enregistrer dans la galerie"
  }

  fun inGallery(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "في المعرض"
    AppLanguage.ENGLISH -> "In Gallery"
    AppLanguage.FRENCH -> "Dans la galerie"
  }

  fun publishedToGalleryBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم النشر بالمعرض ✓"
    AppLanguage.ENGLISH -> "Published ✓"
    AppLanguage.FRENCH -> "Publié ✓"
  }

  fun publishedToGallerySuccess(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم نشر الملف في معرض الصور بنجاح ✓"
    AppLanguage.ENGLISH -> "Saved to Gallery successfully ✓"
    AppLanguage.FRENCH -> "Enregistré dans la galerie avec succès ✓"
  }

  fun publishedToGalleryError(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تعذر نشر الملف في المعرض"
    AppLanguage.ENGLISH -> "Failed to save to Gallery"
    AppLanguage.FRENCH -> "Échec de l'enregistrement dans la galerie"
  }

  fun internalPlayerTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مشغل خاطف الداخلي"
    AppLanguage.ENGLISH -> "Khatif In-App Player"
    AppLanguage.FRENCH -> "Lecteur intégré Khatif"
  }

  fun audioPlaying(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تشغيل ملف صوتي"
    AppLanguage.ENGLISH -> "Playing Audio Track"
    AppLanguage.FRENCH -> "Lecture audio en cours"
  }

  fun fontSizeTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حجم الخط ونصوص التطبيق"
    AppLanguage.ENGLISH -> "Font Size"
    AppLanguage.FRENCH -> "Taille de la police"
  }

  fun fontSizeSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحكم في تكبير أو تصغير الخط لراحة أكبر أثناء القراءة"
    AppLanguage.ENGLISH -> "Adjust text scale for better readability"
    AppLanguage.FRENCH -> "Ajustez la taille du texte pour une meilleure lisibilité"
  }

  fun fontSizeSmall(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "صغير"
    AppLanguage.ENGLISH -> "Small"
    AppLanguage.FRENCH -> "Petit"
  }

  fun fontSizeNormal(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "افتراضي"
    AppLanguage.ENGLISH -> "Default"
    AppLanguage.FRENCH -> "Par défaut"
  }

  fun fontSizeLarge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "كبير"
    AppLanguage.ENGLISH -> "Large"
    AppLanguage.FRENCH -> "Grand"
  }

  fun fontSizeHuge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "كبير جداً"
    AppLanguage.ENGLISH -> "Extra Large"
    AppLanguage.FRENCH -> "Très grand"
  }

  fun analyzingTipColdStart(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الخادم المجاني يستيقظ لأول مرة، هذا أمر طبيعي ولن يطول أكثر من ذلك..."
    AppLanguage.ENGLISH -> "Free server is waking up from standby, this is normal and will complete shortly..."
    AppLanguage.FRENCH -> "Le serveur gratuit se réveille, c'est normal et sera bientôt prêt..."
  }

  fun analyzingTipExtracting(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاري الاتصال واستخراج الجودات المتاحة بأعلى دقة..."
    AppLanguage.ENGLISH -> "Extracting stream qualities in highest resolution..."
    AppLanguage.FRENCH -> "Extraction des flux vidéo en haute résolution..."
  }

  fun analyzingTipFinishing(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "قاربت العملية على الانتهاء، شكرًا لصبرك لحظات قليلة..."
    AppLanguage.ENGLISH -> "Almost ready, thank you for your patience..."
    AppLanguage.FRENCH -> "Presque terminé, merci pour votre patience..."
  }

  fun secondsUnit(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "ثانية"
    AppLanguage.ENGLISH -> "sec"
    AppLanguage.FRENCH -> "sec"
  }

  // Settings Screen
  fun settingsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إعدادات خاطف"
    AppLanguage.ENGLISH -> "Khatif Settings"
    AppLanguage.FRENCH -> "Paramètres de Khatif"
  }

  fun settingsSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تخصيص اللغة ومسار الحفظ وجودة التنزيل والمنصات"
    AppLanguage.ENGLISH -> "Configure language, storage path, download quality and platforms"
    AppLanguage.FRENCH -> "Configurer la langue, l'emplacement, la qualité et les plateformes"
  }

  fun appearanceSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "المظهر والثيم"
    AppLanguage.ENGLISH -> "Appearance & Theme"
    AppLanguage.FRENCH -> "Apparence & Thème"
  }

  fun appearanceDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اختر نمط العرض المريح لعينيك:"
    AppLanguage.ENGLISH -> "Choose your preferred eye-friendly theme:"
    AppLanguage.FRENCH -> "Choisissez le mode d'affichage reposant :"
  }

  fun themeSystem(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تلقائي"
    AppLanguage.ENGLISH -> "Auto"
    AppLanguage.FRENCH -> "Auto"
  }

  fun themeLight(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "فاتح هادئ"
    AppLanguage.ENGLISH -> "Calm Light"
    AppLanguage.FRENCH -> "Clair Calme"
  }

  fun themeDark(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "داكن مريح"
    AppLanguage.ENGLISH -> "Restful Dark"
    AppLanguage.FRENCH -> "Sombre Reposant"
  }

  fun themeSystemSub(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "النظام"
    AppLanguage.ENGLISH -> "System"
    AppLanguage.FRENCH -> "Système"
  }

  fun themeLightSub(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نهاري"
    AppLanguage.ENGLISH -> "Light"
    AppLanguage.FRENCH -> "Clair"
  }

  fun themeDarkSub(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "ليلي"
    AppLanguage.ENGLISH -> "Dark"
    AppLanguage.FRENCH -> "Sombre"
  }

  fun languageSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اللغة"
    AppLanguage.ENGLISH -> "Language"
    AppLanguage.FRENCH -> "Langue"
  }

  fun languagePrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اختر لغة التطبيق المفضلة:"
    AppLanguage.ENGLISH -> "Select preferred application language:"
    AppLanguage.FRENCH -> "Sélectionnez la langue de l'application :"
  }

  fun platformsAndGuideSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "المنصات المدعومة ودليل الاستخدام"
    AppLanguage.ENGLISH -> "Supported Platforms & User Guide"
    AppLanguage.FRENCH -> "Plateformes supportées & Guide"
  }

  fun supportedPlatformsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "المنصات المدعومة بالكامل:"
    AppLanguage.ENGLISH -> "Fully Supported Platforms:"
    AppLanguage.FRENCH -> "Plateformes entièrement supportées :"
  }

  fun howToStepsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "خطوات التحميل السريع:"
    AppLanguage.ENGLISH -> "Quick Download Steps:"
    AppLanguage.FRENCH -> "Étapes de téléchargement rapide :"
  }

  fun step1(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "1. انسخ رابط الفيديو من التطبيق (تيك توك، يوتيوب، ريلز، فيسبوك، أو X)."
    AppLanguage.ENGLISH -> "1. Copy video URL from the source app (TikTok, YouTube, Reels, Facebook, or X)."
    AppLanguage.FRENCH -> "1. Copiez l'URL de la vidéo depuis l'application source (TikTok, YouTube, Reels, FB ou X)."
  }

  fun step2(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "2. افتح تطبيق خاطف، واضغط 'لصق'، ثم انقر 'تحميل الفيديو الآن'."
    AppLanguage.ENGLISH -> "2. Open Khatif, tap 'Paste', then tap 'Download Video Now'."
    AppLanguage.FRENCH -> "2. Ouvrez Khatif, appuyez sur 'Coller', puis sur 'Télécharger la vidéo'."
  }

  fun step3(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "3. حدد الجودة المطلوبة (1080p أو 720p أو MP3) واضغط 'تحميل الآن'."
    AppLanguage.ENGLISH -> "3. Choose quality (1080p, 720p, or MP3) and tap 'Download Now'."
    AppLanguage.FRENCH -> "3. Choisissez la qualité (1080p, 720p ou MP3) et appuyez sur 'Télécharger'."
  }

  fun downloadSettingsSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تفضيلات التخزين والتنزيل"
    AppLanguage.ENGLISH -> "Storage & Download Preferences"
    AppLanguage.FRENCH -> "Préférences de stockage & téléchargement"
  }

  fun savePathTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "موقع الحفظ الافتراضي"
    AppLanguage.ENGLISH -> "Default Save Location"
    AppLanguage.FRENCH -> "Dossier d'enregistrement"
  }

  fun changeButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تغيير"
    AppLanguage.ENGLISH -> "Change"
    AppLanguage.FRENCH -> "Modifier"
  }

  fun defaultQualityTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جودة التنزيل الافتراضية"
    AppLanguage.ENGLISH -> "Default Download Quality"
    AppLanguage.FRENCH -> "Qualité par défaut"
  }

  fun selectButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحديد"
    AppLanguage.ENGLISH -> "Select"
    AppLanguage.FRENCH -> "Choisir"
  }

  fun wifiOnlyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "التحميل عبر Wi-Fi فقط"
    AppLanguage.ENGLISH -> "Download on Wi-Fi Only"
    AppLanguage.FRENCH -> "Télécharger via Wi-Fi uniquement"
  }

  fun wifiOnlySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "لتوفير بيانات باقة الهاتف"
    AppLanguage.ENGLISH -> "Preserve mobile cellular data"
    AppLanguage.FRENCH -> "Économiser les données mobiles"
  }

  fun notificationsTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إشعارات اكتمال التحميل"
    AppLanguage.ENGLISH -> "Download Notifications"
    AppLanguage.FRENCH -> "Notifications de téléchargement"
  }

  fun notificationsSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تنبيه فوري عند انتهاء تنزيل وحفظ الفيديو"
    AppLanguage.ENGLISH -> "Instant alert when video download finishes"
    AppLanguage.FRENCH -> "Alerte immédiate à la fin du téléchargement"
  }

  fun downloadPreferencesSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "خيارات التنزيل والأتمتة"
    AppLanguage.ENGLISH -> "Download & Automation Settings"
    AppLanguage.FRENCH -> "Téléchargement & Automatisation"
  }

  fun downloadPreferencesSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الجودة التلقائية، المعرض، وحفظ البيانات"
    AppLanguage.ENGLISH -> "Auto-save, default quality, and network"
    AppLanguage.FRENCH -> "Sauvegarde auto, qualité et réseau"
  }

  fun autoAnalyzeOnPasteTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "التحليل التلقائي فور اللصق"
    AppLanguage.ENGLISH -> "Auto-Analyze on Paste"
    AppLanguage.FRENCH -> "Analyse auto au collage"
  }

  fun autoAnalyzeOnPasteSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "بدء استخراج الفيديو فورًا بمجرد الضغط على زر اللصق"
    AppLanguage.ENGLISH -> "Start extracting media immediately upon tapping paste"
    AppLanguage.FRENCH -> "Lancer l'analyse immédiatement après avoir collé"
  }

  fun autoExportGalleryTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الحفظ التلقائي في المعرض"
    AppLanguage.ENGLISH -> "Auto-Save to Gallery"
    AppLanguage.FRENCH -> "Sauvegarde auto dans la galerie"
  }

  fun autoExportGallerySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نقل الوسائط تلقائيًا إلى استوديو الهاتف فور انتهاء التنزيل"
    AppLanguage.ENGLISH -> "Automatically export media to device gallery when download finishes"
    AppLanguage.FRENCH -> "Exporter automatiquement vers la galerie à la fin du téléchargement"
  }

  fun defaultQualitySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحديد الجودة المفضلة مسبقًا عند فحص أي رابط جديد"
    AppLanguage.ENGLISH -> "Pre-select preferred quality when inspecting any new video"
    AppLanguage.FRENCH -> "Présélectionner la qualité lors de l'analyse d'un lien"
  }

  fun hapticFeedbackTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الاهتزاز التفاعلي (Haptic Touch)"
    AppLanguage.ENGLISH -> "Haptic Touch Feedback"
    AppLanguage.FRENCH -> "Retour haptique"
  }

  fun hapticFeedbackSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "استجابة اهتزازية لطيفة عند الضغط على الأزرار واكتمال التحميل"
    AppLanguage.ENGLISH -> "Subtle vibration on button presses and download completion"
    AppLanguage.FRENCH -> "Vibration tactile lors des clics et de la fin du téléchargement"
  }

  fun clearAllHistoryTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مسح سجل التحميلات بالكامل"
    AppLanguage.ENGLISH -> "Clear All Download History"
    AppLanguage.FRENCH -> "Effacer tout l'historique"
  }

  fun clearAllHistorySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إفراغ قائمة التنزيلات وحذف ملفاتها لتوفير مساحة التخزين"
    AppLanguage.ENGLISH -> "Wipe history entries and delete files to free up device storage"
    AppLanguage.FRENCH -> "Effacer l'historique et supprimer les fichiers téléchargés"
  }

  fun clearAllHistoryConfirm(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "هل أنت متأكد من مسح جميع عناصر السجل وحذف ملفاتها بالكامل من الجهاز؟"
    AppLanguage.ENGLISH -> "Are you sure you want to delete all history items and downloaded files?"
    AppLanguage.FRENCH -> "Voulez-vous vraiment supprimer tout l'historique et les fichiers téléchargés ?"
  }

  fun clearCacheTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تنظيف الذاكرة المؤقتة (Cache)"
    AppLanguage.ENGLISH -> "Clear Temporary Cache"
    AppLanguage.FRENCH -> "Vider la mémoire cache"
  }

  fun cleanButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تنظيف"
    AppLanguage.ENGLISH -> "Clean"
    AppLanguage.FRENCH -> "Vider"
  }

  fun cacheOccupied(lang: AppLanguage, size: String) = when (lang) {
    AppLanguage.ARABIC -> "المساحة المشغولة: $size"
    AppLanguage.ENGLISH -> "Occupied storage: $size"
    AppLanguage.FRENCH -> "Espace occupé : $size"
  }

  fun aboutSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حول"
    AppLanguage.ENGLISH -> "About"
    AppLanguage.FRENCH -> "À propos"
  }

  fun aboutVersion(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الإصدار ${com.example.BuildConfig.VERSION_NAME} (Khatif)"
    AppLanguage.ENGLISH -> "Version ${com.example.BuildConfig.VERSION_NAME} (Khatif)"
    AppLanguage.FRENCH -> "Version ${com.example.BuildConfig.VERSION_NAME} (Khatif)"
  }

  fun versionLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الإصدار"
    AppLanguage.ENGLISH -> "Version"
    AppLanguage.FRENCH -> "Version"
  }

  fun tapToViewDetails(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اضغط لعرض الإصدار والسياسات والشروط"
    AppLanguage.ENGLISH -> "Tap to view version & policies"
    AppLanguage.FRENCH -> "Appuyez pour voir la version et les conditions"
  }

  fun tapToClose(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اضغط للإغلاق"
    AppLanguage.ENGLISH -> "Tap to close"
    AppLanguage.FRENCH -> "Appuyez pour fermer"
  }

  fun aboutDescription(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تطبيق عالي السرعة لتحميل مقاطع الفيديو من شبكات التواصل الاجتماعي بأعلى دقة ممكنة مع واجهة راقية ومريحة للعين."
    AppLanguage.ENGLISH -> "High-speed app for downloading social media videos at top quality with an eye-friendly elegant interface."
    AppLanguage.FRENCH -> "Application ultra-rapide pour télécharger des vidéos depuis les réseaux sociaux en haute qualité avec une interface reposante."
  }

  fun privacyPolicyTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "سياسة الخصوصية والشروط"
    AppLanguage.ENGLISH -> "Privacy Policy & Terms"
    AppLanguage.FRENCH -> "Politique de confidentialité & Conditions"
  }

  fun privacyPolicySubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حماية البيانات والتخزين المحلي"
    AppLanguage.ENGLISH -> "Data protection and local storage info"
    AppLanguage.FRENCH -> "Protection des données et stockage local"
  }

  fun viewButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "عرض"
    AppLanguage.ENGLISH -> "View"
    AppLanguage.FRENCH -> "Voir"
  }

  fun privacyDialogTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "سياسة الخصوصية وشروط الاستخدام"
    AppLanguage.ENGLISH -> "Privacy Policy & Terms of Service"
    AppLanguage.FRENCH -> "Politique de confidentialité & Conditions"
  }

  fun privacyDataSectionTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "1. الخصوصية وحماية البيانات"
    AppLanguage.ENGLISH -> "1. Privacy & Data Protection"
    AppLanguage.FRENCH -> "1. Confidentialité et données"
  }

  fun privacyDataSectionContent(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تطبيق «خاطف» يلتزم بالخصوصية التامة للمستخدم. لا نقوم بجمع أو تسجيل أو مشاركة أي بيانات شخصية، أو حسابات مستخدمين، أو سجلات تصفح. يتم استخدام الروابط المدخلة لحظيًا فقط للتواصل مع خادم التحليل واستخراج الوسائط المباشرة دون أي تعقب."
    AppLanguage.ENGLISH -> "Khatif is strictly private. We do not collect, track, store, or share any personal data, user accounts, or browsing history. Submitted URLs are analyzed in real time solely to resolve media streams without tracking."
    AppLanguage.FRENCH -> "Khatif respecte totalement votre vie privée. Aucune donnée personnelle, compte ou historique n'est collecté ou partagé. Les liens sont analysés uniquement en temps réel pour extraire les médias."
  }

  fun privacyStorageSectionTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "2. التخزين والأذونات والملفات"
    AppLanguage.ENGLISH -> "2. Storage, Permissions & Files"
    AppLanguage.FRENCH -> "2. Stockage et autorisations"
  }

  fun privacyStorageSectionContent(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "يتم تخزين جميع الفيديوهات والتسجيلات الصوتية داخل التخزين المحلي والخاص بالتطبيق على جهازك، دون طلب أذونات وصول واسعة. وعند اختيارك «نشر في المعرض»، تُستخدم واجهات MediaStore الرسمية والآمنة لنظام أندرويد."
    AppLanguage.ENGLISH -> "All downloaded videos and audio clips are saved into app-private internal storage without requiring intrusive permissions. Exporting to the Gallery relies exclusively on official Android MediaStore APIs."
    AppLanguage.FRENCH -> "Tous les fichiers téléchargés sont conservés dans le stockage privé de l'application. La publication dans la galerie utilise exclusivement les API sécurisées Android MediaStore."
  }

  fun privacyTermsSectionTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "3. شروط الاستخدام والملكية الفكرية"
    AppLanguage.ENGLISH -> "3. Terms of Use & Intellectual Property"
    AppLanguage.FRENCH -> "3. Conditions d'utilisation & Droits"
  }

  fun privacyTermsSectionContent(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تطبيق «خاطف» أداة تقنية مخصصة للاستخدام الشخصي المشروع فقط ولحفظ النسخ الاحتياطية للمحتوى المتاح للمشاهدة العامة. يتحمل المستخدم المسؤولية الكاملة عن احترام حقوق النشر والتراخيص الخاصة بأصحاب المحتوى الأصليين."
    AppLanguage.ENGLISH -> "Khatif is a tool intended for personal, lawful, offline backup use of publicly available content. Users are solely responsible for honoring original creators' copyrights and licensing terms."
    AppLanguage.FRENCH -> "Khatif est un outil destiné à un usage personnel et légal de sauvegarde de contenus publics. L'utilisateur est seul responsable du respect des droits d'auteur des créateurs."
  }

  fun privacyAgreeButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "فهمت وموافق"
    AppLanguage.ENGLISH -> "Understood & Agreed"
    AppLanguage.FRENCH -> "Compris et accepté"
  }

  fun buildTypeLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نوع البناء"
    AppLanguage.ENGLISH -> "Build Type"
    AppLanguage.FRENCH -> "Type de build"
  }

  fun buildTypeValue(lang: AppLanguage, buildType: String = com.example.BuildConfig.BUILD_TYPE): String {
    val type = buildType.lowercase()
    return when (lang) {
      AppLanguage.ARABIC -> when {
        type.contains("debug") -> "نسخة تجريبية (Debug)"
        type.contains("release") -> "نسخة مستقرة (Release)"
        else -> "نسخة (${buildType.replaceFirstChar { it.uppercase() }})"
      }
      AppLanguage.ENGLISH -> when {
        type.contains("debug") -> "Debug Build"
        type.contains("release") -> "Release Build"
        else -> "${buildType.replaceFirstChar { it.uppercase() }} Build"
      }
      AppLanguage.FRENCH -> when {
        type.contains("debug") -> "Version Débogage (Debug)"
        type.contains("release") -> "Version Stable (Release)"
        else -> "Version (${buildType.replaceFirstChar { it.uppercase() }})"
      }
    }
  }

  fun engineLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "محرك التنزيل"
    AppLanguage.ENGLISH -> "Download Engine"
    AppLanguage.FRENCH -> "Moteur de téléchargement"
  }

  fun engineValue(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "Khatif Core Engine v${com.example.BuildConfig.VERSION_NAME}"
    AppLanguage.ENGLISH -> "Khatif Core Engine v${com.example.BuildConfig.VERSION_NAME}"
    AppLanguage.FRENCH -> "Moteur Khatif Core v${com.example.BuildConfig.VERSION_NAME}"
  }

  fun qualityDialogTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الجودة الافتراضية للتحميل"
    AppLanguage.ENGLISH -> "Default Download Quality"
    AppLanguage.FRENCH -> "Qualité de téléchargement par défaut"
  }

  fun qualityDialogPrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اختر الجودة التي ترغب في تطبيقها تلقائياً عند تحميل الفيديوهات:"
    AppLanguage.ENGLISH -> "Choose default quality applied when downloading new videos:"
    AppLanguage.FRENCH -> "Choisissez la qualité appliquée par défaut lors du téléchargement :"
  }

  fun pathDialogTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "موقع حفظ الفيديوهات"
    AppLanguage.ENGLISH -> "Default Save Location"
    AppLanguage.FRENCH -> "Emplacement d'enregistrement"
  }

  fun pathDialogPrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حدد المجلد الذي سيتم حفظ الفيديوهات بداخله تلقائياً:"
    AppLanguage.ENGLISH -> "Select the folder where videos will be stored automatically:"
    AppLanguage.FRENCH -> "Sélectionnez le dossier où enregistrer automatiquement les vidéos :"
  }

  fun customPathLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مسار مخصص"
    AppLanguage.ENGLISH -> "Custom Path"
    AppLanguage.FRENCH -> "Chemin personnalisé"
  }

  fun savePathButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حفظ المسار"
    AppLanguage.ENGLISH -> "Save Path"
    AppLanguage.FRENCH -> "Enregistrer"
  }

  fun clearCacheDialogTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تنظيف الذاكرة المؤقتة"
    AppLanguage.ENGLISH -> "Clear Cache"
    AppLanguage.FRENCH -> "Vider le cache"
  }

  fun clearCacheConfirmPrompt(lang: AppLanguage, size: String) = when (lang) {
    AppLanguage.ARABIC -> "هل ترغب في تفريغ الذاكرة المؤقتة ($size)؟\nلن يتم حذف الفيديوهات المحفوظة في معرض جهازك."
    AppLanguage.ENGLISH -> "Do you want to free up cache memory ($size)?\nYour saved videos in your device gallery will not be deleted."
    AppLanguage.FRENCH -> "Voulez-vous libérer la mémoire cache ($size) ?\nVos vidéos enregistrées dans votre galerie ne seront pas supprimées."
  }

  fun clearCacheEmptyPrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الذاكرة المؤقتة فارغة بالفعل (0.0 MB). لا توجد ملفات لحذفها."
    AppLanguage.ENGLISH -> "Cache is already empty (0.0 MB). No files to clear."
    AppLanguage.FRENCH -> "La mémoire cache est déjà vide (0.0 MB)."
  }

  fun clearNowButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تنظيف الآن"
    AppLanguage.ENGLISH -> "Clear Now"
    AppLanguage.FRENCH -> "Vider maintenant"
  }

  // Quality option localized descriptions
  fun qualityDesc(lang: AppLanguage, option: String) = when (lang) {
    AppLanguage.ARABIC -> when (option) {
      "1080p Full HD" -> "دقة فائقة الوضوح (أعلى نقاء)"
      "720p HD" -> "دقة عالية متوازنة (موصى بها)"
      "480p SD" -> "دقة قياسية (توفير البيانات)"
      else -> "استخراج الصوت فقط بدون فيديو"
    }
    AppLanguage.ENGLISH -> when (option) {
      "1080p Full HD" -> "Ultra high definition (sharpest picture)"
      "720p HD" -> "Balanced high definition (recommended)"
      "480p SD" -> "Standard definition (saves data)"
      else -> "Pure audio extraction without video"
    }
    AppLanguage.FRENCH -> when (option) {
      "1080p Full HD" -> "Très haute définition (image la plus nette)"
      "720p HD" -> "Haute définition équilibrée (recommandée)"
      "480p SD" -> "Définition standard (économise les données)"
      else -> "Extraction audio pure sans vidéo"
    }
  }

  fun pathPresetDesc(lang: AppLanguage, path: String) = when (lang) {
    AppLanguage.ARABIC -> when {
      path.contains("Download") -> "مجلد التنزيلات (الافتراضي)"
      path.contains("Movies") -> "مجلد الأفلام والفيديوهات"
      path.contains("DCIM") -> "مجلد الكاميرا والمعرض"
      else -> "مجلد المستندات"
    }
    AppLanguage.ENGLISH -> when {
      path.contains("Download") -> "Downloads folder (Default)"
      path.contains("Movies") -> "Movies & Videos folder"
      path.contains("DCIM") -> "Camera & Gallery folder"
      else -> "Documents folder"
    }
    AppLanguage.FRENCH -> when {
      path.contains("Download") -> "Dossier Téléchargements (Par défaut)"
      path.contains("Movies") -> "Dossier Films et Vidéos"
      path.contains("DCIM") -> "Dossier Galerie & Photos"
      else -> "Dossier Documents"
    }
  }

  // Snackbar notifications
  fun pasteValidUrlMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "يرجى لصق رابط فيديو أولاً!"
    AppLanguage.ENGLISH -> "Please paste a video link first!"
    AppLanguage.FRENCH -> "Veuillez d'abord coller un lien vidéo !"
  }

  fun downloadCompleteMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم التحميل بنجاح وتم الحفظ في السجل! 🎉"
    AppLanguage.ENGLISH -> "Download completed and saved to history! 🎉"
    AppLanguage.FRENCH -> "Téléchargement terminé et enregistré dans l'historique ! 🎉"
  }

  fun itemDeletedMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم حذف العنصر من سجل التنزيلات"
    AppLanguage.ENGLISH -> "Item deleted from history"
    AppLanguage.FRENCH -> "Élément supprimé de l'historique"
  }

  fun historyClearedMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم مسح سجل التنزيلات بالكامل"
    AppLanguage.ENGLISH -> "Download history cleared completely"
    AppLanguage.FRENCH -> "Historique des téléchargements entièrement effacé"
  }

  fun defaultQualitySetMessage(lang: AppLanguage, quality: String) = when (lang) {
    AppLanguage.ARABIC -> "تم تعيين الجودة الافتراضية: $quality"
    AppLanguage.ENGLISH -> "Default quality set: $quality"
    AppLanguage.FRENCH -> "Qualité par défaut définie : $quality"
  }

  fun savePathSetMessage(lang: AppLanguage, path: String) = when (lang) {
    AppLanguage.ARABIC -> "تم تعيين مسار الحفظ: $path"
    AppLanguage.ENGLISH -> "Save path set: $path"
    AppLanguage.FRENCH -> "Emplacement de sauvegarde défini : $path"
  }

  fun cacheClearedMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم تنظيف الذاكرة المؤقتة بنجاح"
    AppLanguage.ENGLISH -> "Cache cleared successfully"
    AppLanguage.FRENCH -> "Mémoire cache vidée avec succès"
  }

  fun clipboardEmptyMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الحافظة فارغة، يرجى نسخ رابط فيديو أولاً"
    AppLanguage.ENGLISH -> "Clipboard is empty, please copy a video link first"
    AppLanguage.FRENCH -> "Le presse-papiers est vide, veuillez d'abord copier un lien vidéo"
  }

  fun backendOnlineBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "خادم التحميل متصل"
    AppLanguage.ENGLISH -> "Download Server Online"
    AppLanguage.FRENCH -> "Serveur de téléchargement connecté"
  }

  fun backendCheckingBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاري الاتصال بالخادم..."
    AppLanguage.ENGLISH -> "Connecting to server..."
    AppLanguage.FRENCH -> "Connexion au serveur..."
  }

  fun backendOfflineBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الخادم قيد التشغيل / غير متاح"
    AppLanguage.ENGLISH -> "Server waking up / offline"
    AppLanguage.FRENCH -> "Serveur hors ligne"
  }

  fun networkErrorMessage(lang: AppLanguage, detail: String? = null): String {
    if (!detail.isNullOrBlank()) return detail
    return when (lang) {
      AppLanguage.ARABIC -> "تعذر جلب الفيديو. تأكد من صحة الرابط أو من اتصال الإنترنت."
      AppLanguage.ENGLISH -> "Could not fetch video. Check the link or your internet connection."
      AppLanguage.FRENCH -> "Impossible de récupérer la vidéo. Vérifiez le lien ou la connexion."
    }
  }

  fun downloadErrorMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حدث خطأ أثناء تنزيل الفيديو، يرجى المحاولة مجددًا"
    AppLanguage.ENGLISH -> "Error occurred during download, please try again"
    AppLanguage.FRENCH -> "Une erreur s'est produite lors du téléchargement"
  }

  fun downloadProgressText(lang: AppLanguage, percent: Int, downloadedMb: String) = when (lang) {
    AppLanguage.ARABIC -> "جاري التحميل $percent% ($downloadedMb)"
    AppLanguage.ENGLISH -> "Downloading $percent% ($downloadedMb)"
    AppLanguage.FRENCH -> "Téléchargement $percent% ($downloadedMb)"
  }

  fun noMediaPlayerFound(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "لا يوجد تطبيق مثبت لتشغيل هذا الملف"
    AppLanguage.ENGLISH -> "No app found to play this media file"
    AppLanguage.FRENCH -> "Aucune application trouvée pour lire ce fichier"
  }

  fun mediaFileNotFound(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تعذر فتح الملف، قد يكون تم نقله أو حذفه"
    AppLanguage.ENGLISH -> "Cannot open file, it may have been moved or deleted"
    AppLanguage.FRENCH -> "Impossible d'ouvrir le fichier, il a peut-être été déplacé ou supprimé"
  }

  fun fileSavedToGallery(lang: AppLanguage, folder: String) = when (lang) {
    AppLanguage.ARABIC -> "تم الحفظ بنجاح في $folder وسيظهر في المعرض مباشرة!"
    AppLanguage.ENGLISH -> "Successfully saved to $folder and visible in Gallery!"
    AppLanguage.FRENCH -> "Enregistré avec succès dans $folder et visible dans la Galerie !"
  }

  fun transferToGallery(lang: AppLanguage, isAudio: Boolean) = when (lang) {
    AppLanguage.ARABIC -> if (isAudio) "نقل إلى ملفات الصوت" else "نقل إلى الاستوديو"
    AppLanguage.ENGLISH -> if (isAudio) "Move to Audio Files" else "Move to Gallery"
    AppLanguage.FRENCH -> if (isAudio) "Déplacer vers Fichiers Audio" else "Déplacer vers Galerie"
  }

  fun transferredBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم النقل ✓"
    AppLanguage.ENGLISH -> "Transferred ✓"
    AppLanguage.FRENCH -> "Transféré ✓"
  }

  fun transferSuccessMessage(lang: AppLanguage, isAudio: Boolean) = when (lang) {
    AppLanguage.ARABIC -> if (isAudio) "تم نقل الملف إلى ملفات الصوت بنجاح ✓" else "تم نقل الملف إلى الاستوديو بنجاح ✓"
    AppLanguage.ENGLISH -> if (isAudio) "Transferred to audio files successfully ✓" else "Transferred to gallery successfully ✓"
    AppLanguage.FRENCH -> if (isAudio) "Transféré vers les fichiers audio avec succès ✓" else "Transféré vers la galerie avec succès ✓"
  }

  fun platformMaintenanceBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "قيد الصيانة"
    AppLanguage.ENGLISH -> "Under Maintenance"
    AppLanguage.FRENCH -> "En maintenance"
  }

  fun platformMaintenanceMessage(lang: AppLanguage, platformName: String) = when (lang) {
    AppLanguage.ARABIC -> "هذه المنصة قيد الصيانة حاليًا، يمكنك تحميل الفيديوهات من تيك توك وفيسبوك في الوقت الحالي"
    AppLanguage.ENGLISH -> "This platform is currently under maintenance. You can download videos from TikTok and Facebook right now."
    AppLanguage.FRENCH -> "Cette plateforme est actuellement en maintenance. Vous pouvez télécharger des vidéos depuis TikTok et Facebook pour le moment."
  }

  fun backToHistory(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "العودة للسجل"
    AppLanguage.ENGLISH -> "Back to History"
    AppLanguage.FRENCH -> "Retour à l'historique"
  }

  fun gridView(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "عرض شبكي"
    AppLanguage.ENGLISH -> "Grid View"
    AppLanguage.FRENCH -> "Vue en grille"
  }

  fun listView(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "عرض قائمة"
    AppLanguage.ENGLISH -> "List View"
    AppLanguage.FRENCH -> "Vue en liste"
  }

  fun mediaInfoTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "معلومات الملف"
    AppLanguage.ENGLISH -> "Media Information"
    AppLanguage.FRENCH -> "Informations sur le média"
  }

  fun fileSizeLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حجم الملف"
    AppLanguage.ENGLISH -> "File Size"
    AppLanguage.FRENCH -> "Taille du fichier"
  }

  fun durationLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مدة التشغيل"
    AppLanguage.ENGLISH -> "Duration"
    AppLanguage.FRENCH -> "Durée"
  }

  fun downloadDateLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تاريخ التحميل"
    AppLanguage.ENGLISH -> "Download Date"
    AppLanguage.FRENCH -> "Date de téléchargement"
  }

  fun qualityLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الدقة والجودة"
    AppLanguage.ENGLISH -> "Resolution & Quality"
    AppLanguage.FRENCH -> "Résolution & Qualité"
  }

  fun platformLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "المنصة الأصلية"
    AppLanguage.ENGLISH -> "Original Platform"
    AppLanguage.FRENCH -> "Plateforme d'origine"
  }

  fun storagePathLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "المسار المحلي"
    AppLanguage.ENGLISH -> "Local Storage Path"
    AppLanguage.FRENCH -> "Emplacement local"
  }

  fun cacheSection(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "الذاكرة المؤقتة"
    AppLanguage.ENGLISH -> "Temporary Cache"
    AppLanguage.FRENCH -> "Mémoire cache"
  }

  fun swipeDownToDismiss(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اسحب للأسفل للإغلاق"
    AppLanguage.ENGLISH -> "Swipe down to close"
    AppLanguage.FRENCH -> "Glisser vers le bas pour fermer"
  }

  // 2 Streamlined Qualities & Audio
  fun bestPhoneQualityLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "أفضل جودة للهاتف (Best Quality)"
    AppLanguage.ENGLISH -> "Best Quality for Phone"
    AppLanguage.FRENCH -> "Meilleure qualité pour téléphone"
  }

  fun bestPhoneQualityDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "أعلى دقة ووضوح متوفر يدعمه هاتفك"
    AppLanguage.ENGLISH -> "Highest resolution supported by your device"
    AppLanguage.FRENCH -> "Résolution maximale supportée par votre appareil"
  }

  fun hdQualityLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جودة HD (عالية الدقة)"
    AppLanguage.ENGLISH -> "HD Quality (High Definition)"
    AppLanguage.FRENCH -> "Qualité HD (Haute Définition)"
  }

  fun hdQualityDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "دقة متوازنة ممتازة وحجم سريع التحميل"
    AppLanguage.ENGLISH -> "Balanced quality, fast download & smooth play"
    AppLanguage.FRENCH -> "Qualité équilibrée et téléchargement rapide"
  }

  fun audioOnlyQualityLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تحميل الصوت فقط (Audio HQ)"
    AppLanguage.ENGLISH -> "Download Audio Only (HQ)"
    AppLanguage.FRENCH -> "Télécharger audio uniquement (HQ)"
  }

  fun audioOnlyQualityDesc(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "استخراج صوت نقي عالي الجودة بحجم صغير جداً"
    AppLanguage.ENGLISH -> "Pure crystal-clear audio with compact size"
    AppLanguage.FRENCH -> "Audio pur et cristallin en format compact"
  }

  fun tabVideo(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "فيديو (HD / أفضل دقة)"
    AppLanguage.ENGLISH -> "Video (HD / Best)"
    AppLanguage.FRENCH -> "Vidéo (HD / Max)"
  }

  fun tabAudio(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "صوت فقط (M4A / MP3)"
    AppLanguage.ENGLISH -> "Audio Only (M4A)"
    AppLanguage.FRENCH -> "Audio seul (M4A)"
  }

  // Rename Feature
  fun renameTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تعديل اسم الملف"
    AppLanguage.ENGLISH -> "Rename File"
    AppLanguage.FRENCH -> "Renommer le fichier"
  }

  fun renamePrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "اكتب الاسم الجديد للملف:"
    AppLanguage.ENGLISH -> "Enter new title for the file:"
    AppLanguage.FRENCH -> "Entrez le nouveau nom du fichier :"
  }

  fun renameConfirm(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "حفظ الاسم"
    AppLanguage.ENGLISH -> "Save Title"
    AppLanguage.FRENCH -> "Enregistrer"
  }

  fun renameSuccessMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم تغيير اسم الملف بنجاح"
    AppLanguage.ENGLISH -> "File renamed successfully"
    AppLanguage.FRENCH -> "Fichier renommé avec succès"
  }

  // Copy Link
  fun copyLinkButton(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نسخ الرابط الأصلي"
    AppLanguage.ENGLISH -> "Copy Original Link"
    AppLanguage.FRENCH -> "Copier le lien source"
  }

  fun linkCopiedMessage(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تم نسخ رابط الفيديو إلى الحافظة بنجاح"
    AppLanguage.ENGLISH -> "Video URL copied to clipboard"
    AppLanguage.FRENCH -> "Lien de la vidéo copié dans le presse-papier"
  }

  // Playback Features
  fun playbackSpeedLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "سرعة التشغيل"
    AppLanguage.ENGLISH -> "Playback Speed"
    AppLanguage.FRENCH -> "Vitesse de lecture"
  }

  fun loopModeLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "تكرار المقطع"
    AppLanguage.ENGLISH -> "Repeat"
    AppLanguage.FRENCH -> "Répéter"
  }

  fun sleepTimerLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "مؤقت النوم"
    AppLanguage.ENGLISH -> "Sleep Timer"
    AppLanguage.FRENCH -> "Minuteur de sommeil"
  }

  fun sleepTimerSetMessage(lang: AppLanguage, minutes: Int) = when (lang) {
    AppLanguage.ARABIC -> "تم ضبط مؤقت النوم على $minutes دقيقة"
    AppLanguage.ENGLISH -> "Sleep timer set for $minutes minutes"
    AppLanguage.FRENCH -> "Minuteur réglé sur $minutes minutes"
  }

  fun sleepTimerOff(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إيقاف المؤقت"
    AppLanguage.ENGLISH -> "Off"
    AppLanguage.FRENCH -> "Désactivé"
  }

  fun pipModeLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "نافذة عائمة (صورة داخل صورة)"
    AppLanguage.ENGLISH -> "Picture-in-Picture"
    AppLanguage.FRENCH -> "Image dans l'image"
  }

  fun lockControlsLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "قفل الأزرار والشاشة"
    AppLanguage.ENGLISH -> "Lock Controls"
    AppLanguage.FRENCH -> "Verrouiller les commandes"
  }

  fun unlockControlsLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "إلغاء قفل الأزرار"
    AppLanguage.ENGLISH -> "Unlock Controls"
    AppLanguage.FRENCH -> "Déverrouiller"
  }

  fun backgroundPlayingNotice(lang: AppLanguage) = when (lang) {
    AppLanguage.ARABIC -> "جاري التشغيل في الخلفية"
    AppLanguage.ENGLISH -> "Playing in Background"
    AppLanguage.FRENCH -> "Lecture en arrière-plan"
  }
}
