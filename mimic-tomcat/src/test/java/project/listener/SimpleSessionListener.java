package project.listener;

import com.attackonarchitect.listener.WebListener;
import com.attackonarchitect.listener.session.SessionEvent;
import com.attackonarchitect.listener.session.SessionListener;

/**
 * 测试会话监听器
 *
 * @author <a href="mailto:675464934@qq.com">Terrdi</a>
 * @date 2024/5/15
 * @since 1.8
 **/
@WebListener
public class SimpleSessionListener implements SessionListener {
    @Override
    public void sessionEvent(SessionEvent event) {
        System.out.println(event.getType() + ", " + event.getData());
    }
}
