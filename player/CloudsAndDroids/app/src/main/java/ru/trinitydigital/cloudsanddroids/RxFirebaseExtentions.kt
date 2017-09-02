package ru.trinitydigital.cloudsanddroids

import android.util.Log
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single

class Types {
    companion object {
        val listOfStatesType = object : GenericTypeIndicator<@JvmSuppressWildcards List<@JvmSuppressWildcards State>>() {}
        val settingsType = object : GenericTypeIndicator<Settings>() {}
        val stateType = object : GenericTypeIndicator<State>() {}
        val turnType = object : GenericTypeIndicator<Turn>() {}
        val cardType = object : GenericTypeIndicator<Card>() {}
    }
}

fun DatabaseReference.getKeysListObservable() = Observable.create<List<String>> { subscriber ->
    this.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            Log.v("getKeysListObservable", "data = ${dataSnapshot.toString()}")
            if (dataSnapshot == null || dataSnapshot.value == null)
                    subscriber.onNext(kotlin.collections.listOf())
            else
                subscriber.onNext(dataSnapshot.children.map { it.key })
        }

        override fun onCancelled(error: DatabaseError) {
            subscriber.onError(error.toException())
        }
    })
}!!

fun <T> DatabaseReference.getValueObservable(clazz: GenericTypeIndicator<T>, defaultValue: T) = Observable.create<T> { subscriber ->
    this.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.v("getValueObservable", "data = ${dataSnapshot.toString()}")
                if (dataSnapshot != null)
                    subscriber.onNext(dataSnapshot.getValue(clazz))
                else
                    subscriber.onNext(defaultValue)
        }

        override fun onCancelled(error: DatabaseError) {
            subscriber.onError(error.toException())
        }
    })
}!!

fun <T> DatabaseReference.getValueSingle(clazz: GenericTypeIndicator<T>, defaultValue: T) = Single.create<T> { subscriber ->
    this.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            Log.v("getValueSingle", "data = ${dataSnapshot.toString()}")
            if (dataSnapshot != null)
                subscriber.onSuccess(dataSnapshot.getValue(clazz))
            else
                subscriber.onSuccess(defaultValue)
        }

        override fun onCancelled(error: DatabaseError) {
            subscriber.onError(error.toException())
        }
    })
}!!

fun <T> Query.getLastValueObservable(clazz: GenericTypeIndicator<T>, defaultValue: T) = Observable.create<T> { subscriber ->
    this.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            Log.v("getLastValueObservable", "data = ${dataSnapshot.toString()}")
                if (dataSnapshot != null) {
                    val list = kotlin.collections.mutableListOf<T>()
                    dataSnapshot.children.mapTo(list) {
                        it.getValue(clazz)
                    }
                    if (list.size > 0)
                        subscriber.onNext(list.last())
                    else
                        subscriber.onNext(defaultValue)
                }
            subscriber.onNext(defaultValue)
        }

        override fun onCancelled(error: DatabaseError) {
            subscriber.onError(error.toException())
        }
    })
}!!