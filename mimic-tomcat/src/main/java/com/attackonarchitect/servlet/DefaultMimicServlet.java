package com.attackonarchitect.servlet;

import com.attackonarchitect.http.HttpMTFileResponse;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.http.MTRequest;
import com.attackonarchitect.http.MTResponse;
import com.attackonarchitect.utils.FileUtil;
import com.attackonarchitect.utils.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * @description:
 */
public class DefaultMimicServlet extends MimicServlet{
    @Override
    protected void doPost(MTRequest req, MTResponse response) {

    }

    @Override
    protected void doGet(MTRequest req, MTResponse response) throws UnsupportedEncodingException {
        final String uri = StringUtil.uriDecode(req.uri());
        File file;
        file = FileUtil.resolveFile(uri);
        HttpMTResponse resp = (HttpMTResponse) response;
        resp.setDelegate(new HttpMTFileResponse(resp, file).buildUri(uri));

//        response.write("this is default servlet. request is wrong if you see this");
    }
}
