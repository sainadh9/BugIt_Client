package com.bugit.bugit.bugappscope.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bugit.bugit.R
import com.bugit.bugit.bugappscope.domain.entities.AddSheet
import com.bugit.bugit.bugappscope.domain.entities.PostCreateSheetModel
import com.bugit.bugit.bugappscope.domain.entities.PostRowToSheet
import com.bugit.bugit.bugappscope.domain.entities.Properties
import com.bugit.bugit.bugappscope.domain.entities.Requests
import com.bugit.bugit.bugappscope.viewmodels.BugitRemoteViewModel
import com.bugit.bugit.databinding.ActivityBugreportBinding
import com.bugit.bugit.utils.Constants.API_KEY
import com.bugit.bugit.utils.Constants.INSERT_ROWS
import com.bugit.bugit.utils.Constants.USER_ENTERED
import com.bugit.bugit.utils.ExtensionFunctions.disable
import com.bugit.bugit.utils.ExtensionFunctions.enable
import com.bugit.bugit.utils.NetworkMonitor
import com.bugit.bugit.utils.UniversalManager
import com.bugit.bugit.utils.UniversalManager.combine
import com.bugit.bugit.utils.UniversalManager.getLocalDateTimeInNumberFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ReportBugActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBugreportBinding
    private lateinit var viewModel: BugitRemoteViewModel
    private lateinit var latestTab: String
    private lateinit var progressDialog: ProgressDialog
    private lateinit var networkMonitor: NetworkMonitor
    private var imageUri: Uri? = null

    private var title = MutableStateFlow("")
    private var description = MutableStateFlow("")
    private var downloadedUrl = MutableStateFlow("")
    private var errorMessage: String? = null


    /**
     * @sainadh  For Validating the Bug Form
     */
    private val isFormValid = combine(
        title, description, downloadedUrl
    )
    { title, des, downloadedUrl ->

        val isTitleValid = title.length >= 3
        val isDescValid = des.length >= 10 && des.length <= 300
        val isImageValid = downloadedUrl.isNotEmpty()



        errorMessage = when {
            !isTitleValid -> {
                getString(R.string.isTitleValid)
            }

            !isDescValid -> {
                getString(R.string.isDescValid)
            }

            !isImageValid -> {
                getString(R.string.isImageValid)
            }


            else -> {
                ""
            }
        }
        errorMessage?.let {
            binding.tvErrortext.text = it
        }
        isTitleValid and isDescValid and isImageValid

    }

    /**
     * When the screen resume lifecycle it starts monitor the form
     */
    private fun checkValidation() {

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                isFormValid.collectLatest {
                    binding.btnSubmit.apply {
                        if (it) {
                            binding.btnSubmit.enable()


                        } else {
                            binding.btnSubmit.disable()


                        }

                    }
                }
            }
        }


    }

    /**
     * When the screen resume lifecycle it starts monitor the form
     */
    private fun checkNewTabCreated(){

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                /**
                 * Here latestTab->Todays Tab
                 * empty or previous date->call getAllSheetData Api to check new sheet created with todays date
                 * if not there->createNewTab Api
                 *
                 */
                viewModel.latestTab.collectLatest {
                    latestTab = it.toString()
                    if (latestTab.isNullOrEmpty() || latestTab != getLocalDateTimeInNumberFormat()) {
                        viewModel.getAllSheetData(API_KEY)
                    }
                }

            }
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBugreportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[BugitRemoteViewModel::class.java]
        initViews()
    }

    private fun initViews() {
        networkMonitor = NetworkMonitor(applicationContext)
        networkMonitor.startMonitoring()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait....")
        progressDialog.setCancelable(false)
        /**
         * @sainadh  For Validating the Bug Form
         */
        checkValidation()
        checkNewTabCreated()
        observers()
        binding.apply {
            //Automatically checks the validation when typing on the air and assigns the value
            etBugTitle.doOnTextChanged { text, start, count, after ->
                title.value = text.toString()
            }
            etBugDescription.doOnTextChanged { text, start, count, after ->
                description.value = text.toString()
            }

            //action->For Picking the image from the gallery.Not the app level its Total Gallary level
            tvSelectImage.setOnClickListener {
                galleryIntent()
            }
            //action->Creating the new Row
            btnSubmit.setOnClickListener {
                createNewRow()
            }
            //action->Resetting the total form
            btnReset.setOnClickListener {
                resetBugActivtiy()
            }
        }

//Calling->To check whether todays tab created or not
        viewModel.getNewTab()

    }

    private fun galleryIntent() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                val selectedImageUri: Uri = data?.data!!
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri
                    viewModel.uploadImageToStorage(
                        selectedImageUri,
                        getString(R.string.image_loading_failed)
                    )

                }
            }

        }

    }

    /**
     * Here we are keeping all the observers of viewmodel
     */
    private fun observers() {
//For showing the progressbar during the Api calls
        viewModel.showProgressBar.observe(this) {
            if (it != null) {
                when (it) {
                    true -> progressDialog.show()
                    else -> progressDialog.dismiss()
                }
            }
        }
        /**
         * For showing the errormessage during the Api calls
         * Generally The backend team maintains the 2 or 3 types of error structures during creating the apis
         * If any error which is human can't read that one will show with general message.But here I am showing the error response
         */

        viewModel.errorMessage.observe(this) {
            if (it != null) {
                UniversalManager.createAlertDialog(this,
                    "", it.toString().trim(),
                    true, getString(R.string.ok),
                    {

                    }, false, "",
                    {}
                )

            }
        }

        /**
         * For Internet Monitoring
         * available->btnSubmit disabled else viceversa
         */
        networkMonitor.isNetworkAvailable.observe(this) {
            if (it != null) {
                binding.apply {
                    if (!it){
                        btnSubmit.disable()
                        binding.tvErrortext.text = getString(R.string.no_internet)
                    } else btnSubmit.enable()

                }


            }
        }


        /**
         * Success response of creating new tab
         */
        viewModel.createSheetResponse.observe(this) {
            if (it != null) {
                viewModel.saveNewTabLocally(getLocalDateTimeInNumberFormat())
                latestTab = getLocalDateTimeInNumberFormat()
                UniversalManager.showToastMethod(this, getString(R.string.success_sheet_created), true)
                //Adding headers to the new sheet
                val inputHeader = resources.getStringArray(R.array.headingArray).toList()
                createNewRow(inputHeader)
            }
        }
        /**
         * Success response of getting All Sheet Data
         */
        viewModel.getAllSheetsResponse.observe(this) {
            if (it != null) {
                val todaysTab = getLocalDateTimeInNumberFormat()
                val tabsList = it.sheets?.map { singleSheet -> singleSheet.properties?.title }
                val isTodaysTabCreated = tabsList?.any({ it == todaysTab })

                if (!isTodaysTabCreated!!) {
                    createNewTab(todaysTab)

                }else
                    viewModel.saveNewTabLocally(getLocalDateTimeInNumberFormat())
            }
        }

        /**
         * Success response of creating row in new sheet
         */
        viewModel.createRowResponse.observe(this) {
            if (it != null) {
                UniversalManager.showToastMethod(this, getString(R.string.success_updated_row), true)
                resetBugActivtiy()
            }
        }

        /**
         * Success response of getting download url
         */
        viewModel.uploadedUrl.observe(this) {
            if (it != null) {
                downloadedUrl.value = it.toString()
                // update the image view in the layout
                binding.ivImageUpload.setImageURI(imageUri)
            }
        }
    }

    //Resetting the whole form
    fun resetBugActivtiy() {
        binding.apply {
            etBugTitle.setText("")
            etBugDescription.setText("")
            ivImageUpload.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@ReportBugActivity,
                    R.drawable.placeholder_image
                )
            )
            downloadedUrl.value = ""

        }
    }

    /**
     * action->Creating New Sheet
     * tabName->SheerName
     */
    fun createNewTab(tabName: String) {
        val addSheet = AddSheet(Properties(tabName))
        val requests = mutableListOf<Requests>()
        requests.add(Requests(addSheet))
        val createSheetModel = PostCreateSheetModel(requests)
        if (latestTab != getLocalDateTimeInNumberFormat()) {
            viewModel.createSheet(
                createSheetModel,
                API_KEY
            )
        }

    }

    /**
     * action->Creating New Row
     * inputHeader not empty->Creating header else user input data to sheet(inputRow)
     *
     */
    fun createNewRow(inputHeader: List<String> = listOf()) {
        binding.apply {
            val inputRow = if (inputHeader.isEmpty()) listOf(
                title.value,
                description.value,
                "2.0",
                downloadedUrl.value,
                getLocalDateTimeInNumberFormat(true)
            ) else inputHeader
            val createRow = PostRowToSheet(listOf(inputRow))
            /**
             * parameters
             * 1st->TabName and A1 to D1 inputing the data
             * createRow->User input data or headers
             */
            viewModel.postCreateRow(
                "$latestTab!A1:D1",
                INSERT_ROWS,
                USER_ENTERED,
                API_KEY,
                createRow
            )


        }
    }


}