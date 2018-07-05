package com.tianheng.client.util.rx;


import com.tianheng.client.model.http.HttpResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/**
 * Created by huyg on 2017/8/21.
 */

public class RxResult {

    /**
     * Rx优雅处理服务器返回
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<HttpResponse<T>, T> handleResult() {
        return upstream ->{
            return upstream.flatMap(result -> {
                        if (result.isSuccess()) {
                            return createData(result.getData());
                        }  else {
                            return Observable.error(new Exception(result.getMessage()));
                        }
                    }

            );
        };
    }

    private static <T> Observable<T> createData(final T t) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(t);
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
