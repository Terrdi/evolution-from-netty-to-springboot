package com.attackonarchitect.listener;

/**
 * @description:
 */

public interface Notifier {
    void notifyListeners(Event event);

    void addListener(final EventListener eventListener);

    void removeListener(final EventListener eventListener);
}
