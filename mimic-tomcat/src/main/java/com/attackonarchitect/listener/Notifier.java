package com.attackonarchitect.listener;

import java.util.concurrent.Future;

/**
 * @description:
 */

public interface Notifier {
    Future<?> notifyListeners(Event event);

    void addListener(final EventListener eventListener);

    void removeListener(final EventListener eventListener);

    void stop();
}
