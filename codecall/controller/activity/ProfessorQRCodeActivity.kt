package br.com.codecall.codecall.controller.activity

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.model.Chamada
import br.com.codecall.codecall.model.Materia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfessorQRCodeActivity : AppCompatActivity() {
    internal var bitmap: Bitmap? = null
    private var image_view_qrcode: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_professor)

        image_view_qrcode = findViewById(R.id.image_view_qrcode)
        val intent = intent
        val idMateria: String = intent.getStringExtra("idMateria")
        gerarChamada(idMateria)
    }

    fun gerarChamada(idMateria: String){
        val db = FirebaseFirestore.getInstance()
        var chamada = Chamada(Date(), idMateria)
        db.collection("chamadas")
            .add(chamada)
            .addOnSuccessListener {
                documentReference -> gerarQRCode(documentReference.id)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Erro ao gerar QR Code", Toast.LENGTH_LONG)
                    .show()
            }
    }

    fun gerarQRCode(id: String) {
        bitmap = TextToImageEncode("{\"idChamada\"=\"${id}\"}")
        image_view_qrcode!!.setImageBitmap(bitmap)
        saveImage(bitmap)  //dando permissao de escrita
    }

    fun saveImage(myBitmap: Bitmap?): String {
        val bytes = ByteArrayOutputStream()
        myBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + "/QRcodeDemonuts"
        )
        // objeto buildado na estrutura do diretorio, caso eu precise

        if (!wallpaperDirectory.exists()) {
            Log.d("diretorio", "" + wallpaperDirectory.mkdirs())
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, Calendar.getInstance()
                .timeInMillis.toString() + ".jpg")
            f.createNewFile()   //dando permissao de escrita
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "Arquivo salvo" )

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""

    }

    @Throws(WriterException::class)
    private fun TextToImageEncode(value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                value,
                BarcodeFormat.QR_CODE,
                QRcodeWidth, QRcodeWidth, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    ContextCompat.getColor(this, R.color.black)
                else
                    ContextCompat.getColor(this, R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    companion object {
        val QRcodeWidth = 500
    }
}
