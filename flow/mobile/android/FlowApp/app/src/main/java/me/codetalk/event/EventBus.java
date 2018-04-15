package me.codetalk.event;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by guobxu on 2017/12/27.
 */

public final class EventBus {

    private static final EventBus instance = new EventBus();

    private PublishSubject<Event> eventSubject = PublishSubject.create();

    public static EventBus getInstance() {
        return instance;
    }

//    public PublishSubject<Event> getEventSubject() {
//        return eventSubject;
//    }

    public static void publish(Event e) {
        instance.eventSubject.onNext(e);
    }

    public static Disposable subscribe(Consumer<Event> consumer) {
        return instance.eventSubject.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
//        return instance.eventSubject.subscribe(consumer); // Note: consumer is blocking
    }


}
