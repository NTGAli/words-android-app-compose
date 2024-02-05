package com.ntg.vocabs.screens.setting


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BackupAndRestoreScreen(navController: NavController, wordViewModel: WordViewModel) {
//
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            Appbar(
//                title = stringResource(R.string.setting),
//                scrollBehavior = scrollBehavior
//            )
//        },
//        content = { innerPadding ->
//
//            Content(paddingValues = innerPadding, navController, wordViewModel)
//
//        }
//    )
//
//}
//
//@Composable
//private fun Content(
//    paddingValues: PaddingValues,
//    navController: NavController,
//    wordViewModel: WordViewModel
//) {
//
//    val ctx = LocalContext.current
//    val openBackupDialog = remember { mutableStateOf(false) }
//    val openRestoreDialog = remember { mutableStateOf(false) }
//    val setBackupOnServer = remember {
//        mutableStateOf(false)
//    }
//    val restoreFromServer = remember {
//        mutableStateOf(false)
//    }
//    val visibleSuccess = remember {
//        mutableStateOf(false)
//    }
//    val share = remember {
//        mutableStateOf(false)
//    }
//    val import = remember {
//        mutableStateOf(false)
//    }
//    val isUserLogged = remember {
//        mutableStateOf(false)
//    }
//
//    isUserLogged.value = wordViewModel.getUserData().asLiveData().observeAsState().value?.email.orEmpty() != ""
//
//
//
//
//    if (setBackupOnServer.value) {
//        BackupOnServer(wordViewModel) {
//            setBackupOnServer.value = false
//            if (it) {
//                ctx.toast(ctx.getString(R.string.backup_done))
//                visibleSuccess.value = true
//
//            } else {
//                ctx.toast(ctx.getString(R.string.backup_failed))
//            }
//        }
//    }
//
//    if (restoreFromServer.value) {
//        RestoreUserDataFromServer(wordViewModel = wordViewModel, email = "") {
//            restoreFromServer.value = false
//            if (it) {
//                visibleSuccess.value = true
//            }
//        }
//    }
//
//    if (visibleSuccess.value) {
//        LaunchedEffect(
//            visibleSuccess
//        ) {
//            delay(2000)
//            visibleSuccess.value = false
//        }
//    }
//
//    if (share.value) {
//        ShareUserBackup(wordViewModel = wordViewModel) {
//            share.value = false
//            openBackupDialog.value = false
//        }
//    }
//
//
//
//
//    ReadBackupFromStorage(launch = import.value) {
//        if (it.orEmpty().isNotEmpty()){
//            wordViewModel.importToDB(it!!){isSucceed ->
//                if (isSucceed){
//                    ctx.toast(ctx.getString(R.string.backup_imported))
//                }else{
//                    ctx.toast(ctx.getString(R.string.file_not_supported))
//                }
//            }
//        }
//        import.value = false
//        openRestoreDialog.value = false
//    }
//
//
//
//    LazyColumn(modifier = Modifier.padding(paddingValues)) {
//
//        item {
//            ItemOption(
//                modifier = Modifier.padding(horizontal = 32.dp), text = stringResource(
//                    id = R.string.backup
//                )
//            ) {
//                openBackupDialog.value = true
//            }
//        }
//
//        item {
//            ItemOption(
//                modifier = Modifier.padding(horizontal = 32.dp), text = stringResource(
//                    id = R.string.restore
//                )
//            ) {
//                openRestoreDialog.value = true
//            }
//        }
//
//    }
//
//
//    if (openBackupDialog.value) {
//        AlertDialog(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            onDismissRequest = {
//                openBackupDialog.value = false
//            },
//            icon = {},
//            title = {
//                Text(text = stringResource(id = R.string.backup))
//            },
//            text = {
//                Column {
//                    ItemOption(
//                        text = stringResource(id = R.string.backup_on_server),
//                        loading = setBackupOnServer,
//                        endIcon = painterResource(id = R.drawable.ok),
//                        visibleWithAnimation = visibleSuccess,
//                        subText = if (isUserLogged.value) null else stringResource(id = R.string.loggin_required)
//                    ) {
//
//                        if (isUserLogged.value){
//                            setBackupOnServer.value = true
//                        }else{
//                            openBackupDialog.value = false
//                            navController.navigate(Screens.InsertEmailScreen.name+"?skip=${false}")
//                        }
//
//                    }
//                    ItemOption(text = stringResource(id = R.string.share), divider = false) {
//                        share.value = true
//                    }
//                }
//            },
//            confirmButton = {},
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        openBackupDialog.value = false
//                    }
//                ) {
//                    Text(stringResource(id = R.string.dismiss))
//                }
//            }
//        )
//    }
//
//    if (openRestoreDialog.value) {
//        AlertDialog(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            onDismissRequest = {
//                openRestoreDialog.value = false
//            },
//            icon = {},
//            title = {
//                Text(text = stringResource(id = R.string.restore))
//            },
//            text = {
//                Column {
//
//                    Text(text = stringResource(id = R.string.attention_for_restore_message))
//
//                    ItemOption(
//                        modifier = Modifier.padding(top = 16.dp),
//                        text = stringResource(id = R.string.restore_from_server),
//                        loading = restoreFromServer,
//                        endIcon = painterResource(id = R.drawable.ok),
//                        visibleWithAnimation = visibleSuccess,
//                        subText = if (isUserLogged.value) null else stringResource(id = R.string.loggin_required)
//
//                    ) {
//                        restoreFromServer.value = true
//                    }
//                    ItemOption(text = stringResource(id = R.string.str_import), divider = false) {
//                        import.value = true
//                    }
//                }
//            },
//            confirmButton = {},
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        openRestoreDialog.value = false
//                    }
//                ) {
//                    Text(stringResource(id = R.string.dismiss))
//                }
//            }
//        )
//    }
//}
//
//@Composable
//private fun BackupOnServer(wordViewModel: WordViewModel, resultCallback: (Boolean) -> Unit) {
//    val owner = LocalLifecycleOwner.current
//    UserBackup(wordViewModel = wordViewModel) { wordData ->
//        wordViewModel.upload(wordData, "alintg14@gmail.com").observe(owner) {
//
//            when (it) {
//                is NetworkResult.Error -> {
//                    timber("BackupUserData :::: ERR ${it.message}")
//                    resultCallback.invoke(false)
//                }
//                is NetworkResult.Loading -> {
//                    timber("BackupUserData :::: LD")
//                }
//                is NetworkResult.Success -> {
//                    timber("BackupUserData :::: ${it.data}")
//                    resultCallback.invoke(true)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ShareUserBackup(wordViewModel: WordViewModel, resultCode: (Int) -> Unit) {
//    val ctx = LocalContext.current
//    val shareFileLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) {
//        resultCode.invoke(
//            it.resultCode
//        )
//    }
//
//    UserBackup(wordViewModel = wordViewModel) { data ->
//
//        val json = Gson().toJson(data)
//
//        val dateOfToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        val backupFile = File(ctx.getExternalFilesDir(null), "Vocab_backup_$dateOfToday.txt")
//
//        backupFile.printWriter().use { out ->
//            json.toString().forEach {
//                out.print(it)
//            }
//        }
//
//        val uri =
//            FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", backupFile)
//        val sendIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_STREAM, uri)
//            type = "text/plain" // MIME type for text files
//        }
//        val shareIntent = Intent.createChooser(sendIntent, null)
//        shareFileLauncher.launch(shareIntent)
//    }
//}
//
//@Composable
//private fun UserBackup(wordViewModel: WordViewModel, callBack: (BackupUserData) -> Unit) {
//    val owner = LocalLifecycleOwner.current
//    wordViewModel.getAllWords().observe(owner) { words ->
//        wordViewModel.getAllValidTimeSpent().observe(owner) { times ->
//            wordViewModel.getAllVocabList().observe(owner) {vocabList ->
//                callBack.invoke(
//                    BackupUserData(words = words, totalTimeSpent = times, vocabList = vocabList)
//                )
//            }
//        }
//    }
//}

