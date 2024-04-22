package project.servlet.subservlet;

import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.http.MTRequest;
import com.attackonarchitect.http.MTResponse;
import com.attackonarchitect.http.session.MTSession;
import com.attackonarchitect.servlet.MimicServlet;
import com.attackonarchitect.servlet.WebServlet;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/4/22
 * @since 1.8
 **/
@WebServlet("/session")
public class SessionServlet extends MimicServlet {
    @Override
    protected void doPost(MTRequest req, MTResponse response) {
        String name = req.getParameter("name");

        MTSession session = req.openSession();
        session.setAttribute("name", name);

        ((HttpMTResponse) response).clear();
        response.write("尝试设置了session: ");
        response.write("<h4>");
        response.write(name);
        response.write("</h4>");
    }

    @Override
    protected void doGet(MTRequest req, MTResponse response) throws UnsupportedEncodingException {
        MTSession session = req.openSession();
        String name = (String) session.getAttribute("name");
        if (Objects.isNull(name)) {
            name = "未设置";
        }
        String doc = "<!DOCTYPE html> \n" +                "<html>\n" +                "<head><meta charset=\"utf-8\"><title>Test</title></head>\n"+                "<body bgcolor=\"#f0f0f0\">\n" +                "<h1 align=\"center\">" +
                name + "</h1>\n";
        ((HttpMTResponse) response).clear();
        response.write(doc);
    }
}
