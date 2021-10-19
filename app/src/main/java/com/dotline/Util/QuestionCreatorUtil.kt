package com.dotline.Util

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.annotation.WorkerThread
import com.dotline.help.URIPathHelper
import com.dotline.model.Selector
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import java.io.File

class QuestionCreatorUtil {
    companion object{
        fun  urisToSelector(clipData: ClipData,context: Context):ArrayList<Selector>{
            var uris=ArrayList<Selector>()
            for (i in 0 until clipData.itemCount) {
                var file= uriToFile(clipData.getItemAt(i).uri,context);
                if (file!=null){
                    uris.add(Selector("",file.name,file.path))

                }
            }
            return  uris;
        }
        fun uriToFile(uri:Uri,context: Context):File?{
            var file:File?=null;
            var path=URIPathHelper().getPath(context,uri)
            if (path!=null) file=File(path);
        return file;
        }

        fun uris(clipData: ClipData):ArrayList<Any>{
            var uris=ArrayList<Any>()
            for (i in 0 until clipData.itemCount) {
                val imageUri = clipData.getItemAt(i).getUri()
                uris.add(imageUri)
            }
            return  uris;
        }
        @WorkerThread
        suspend fun compreesImages(uris:ArrayList<Uri>,context: Context):ArrayList<String>{
            val uriPathHelper = URIPathHelper()
            var compressedImages=ArrayList<String>();
            for (uri in uris){
                var path=uriPathHelper.getPath(context,uri);
                if (path != null) {
                    Log.i("path",path)
                }; else return compressedImages;
                var file= File(path);
                var des= File(Environment.getExternalStorageDirectory().path+"/dotLine/Images/compressed${file.name}")
                val compressedImageFile = Compressor.compress(context, file) {
                    quality(40)
                    format(Bitmap.CompressFormat.JPEG)
                    destination(des)
                }

                compressedImages.add(compressedImageFile.path)
            }
            return compressedImages
        }
    }
}