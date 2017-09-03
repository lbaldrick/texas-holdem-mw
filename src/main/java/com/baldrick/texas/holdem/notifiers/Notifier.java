package com.baldrick.texas.holdem.notifiers;

import java.util.function.Consumer;

public interface Notifier<T> {

   public void notify(T t);

   public Consumer<T> getNotifier();
}
