package com.baldrick.texas.holdem.notifiers;

import java.util.function.Consumer;

public interface Notifier<T, S> {

   public void notify(T t, S s);
}
