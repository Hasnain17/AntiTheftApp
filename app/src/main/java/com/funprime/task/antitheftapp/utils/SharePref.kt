package com.funprime.task.antitheftapp.utils
/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 17/06/2023
 */
import android.content.Context
import android.content.SharedPreferences

class SharePref(context: Context)  {
    private val chargerDetection = "CHARGE_SELECTION"
    private val motionDetection = "MOTION_SELECTION"
    private val pocketRemoval = "POCKET_REMOVAL"


    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    init {
        sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    //Charger
    fun putForCharge(value:Boolean)
    {
        editor.putBoolean(chargerDetection,value).apply()
    }

    fun getForCharge():Boolean
    {
        return sharedPreferences.getBoolean(chargerDetection,false)
    }
    fun removeForCharge(){
        editor.remove(chargerDetection).apply()
        editor.commit()
    }

    //Motion
    fun putForMotion(value:Boolean)
    {
        editor.putBoolean(motionDetection,value).apply()
    }

    fun getForMotion():Boolean
    {
        return sharedPreferences.getBoolean(motionDetection,false)
    }
    fun removeForMotion(){
        editor.remove(motionDetection).apply()
        editor.commit()
    }

    //PocketRemoval
    fun putForPocketRemoval(value:Boolean)
    {
        editor.putBoolean(pocketRemoval,value).apply()
    }

    fun getForPocketRemoval():Boolean
    {
        return sharedPreferences.getBoolean(pocketRemoval,false)
    }
    fun removeForPocketRemoval(){
        editor.remove(pocketRemoval).apply()
        editor.commit()
    }
    //Clear All
    fun clearAll(){
        editor.clear().apply()
        editor.commit()
    }
}