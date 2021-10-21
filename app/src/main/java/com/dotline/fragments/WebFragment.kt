package com.dotline.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.dotline.R
import kotlinx.android.synthetic.main.web_fragment.*

class WebFragment:DialogFragment() {
    var url:String?=null;
    var title:String?=null;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
            url=requireArguments().getString("url");
            title=requireArguments().getString("title");
        }
        return inflater.inflate(R.layout.web_fragment,container,false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);

        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        var webView=view.findViewById<WebView>(R.id.web_view);
        webView.webViewClient= WebViewClient();
        webView.settings.javaScriptEnabled=true;
        if (url!=null){
            webView.loadUrl(url!!);
        }
        if (title!=null) toolbar.subtitle=title;


    }
}