package com.plusultra.textrecognition.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.plusultra.textrecognition.databinding.FragmentMainBinding
import java.util.*


class MainFragment : Fragment() {

    private var binding : FragmentMainBinding? = null
    private val bind get() = binding!!
    private var textResult = ""
    private var imgBitmap : Bitmap? = null
    val REQUEST_IMAGE_CAPTURE = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun notFound() {
        Snackbar.make(requireView(), "No Text Found", Snackbar.LENGTH_LONG).show()
    }
    private fun initAction() {
        bind.apply {
            btnDetect.setOnClickListener {
                detect()
            }

            btnSnap.setOnClickListener {
                dispatchTakePictureIntent()
            }

            btnNavigate.setOnClickListener {
                if (textResult.isNotEmpty()) findNavController().navigate(MainFragmentDirections.actionMainFragmentToResutFragment(textResult))
                else notFound()
            }



            btnUpload.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().getReference("TextRecognition")
                val trId = ref.push().key
                val data = mapOf("trId" to "$trId", "Date" to "${Date()}", "Text" to bind.tvResult.text.toString())
                if (trId != null && textResult.isNotEmpty()) {
                    val send = ref.child(trId).setValue(data)
                    send.addOnSuccessListener {
                        Snackbar.make(requireView(), "Success Upload", Snackbar.LENGTH_LONG).show()
                    }
                    send.addOnFailureListener {
                        Snackbar.make(requireView(), "Failed to Upload ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                } else notFound()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQUEST_IMAGE_CAPTURE && resultCode === RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
            val extras = data!!.extras
            imgBitmap = (extras!!["data"] as Bitmap?)!!

            // below line is to set the
            // image bitmap to our image.
            bind.ivPhoto.setImageBitmap(imgBitmap)
        }
    }


    private fun detect() {
        if (imgBitmap != null) {
            val image = InputImage.fromBitmap(imgBitmap!!, 0)
            val detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val result = detector.process(image)
            result.addOnSuccessListener {
                processText(it)
            }
            result.addOnFailureListener {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processText(text: Text) {

        if (text.textBlocks.size == 0) {
            notFound()
        }


        for (block in text.textBlocks) {
            val blockText = block.text
            textResult += blockText + "\n"
        }
        bind.tvResult.text = textResult
    }


    override fun onDetach() {
        super.onDetach()
        binding = null
    }

}