package org.techtown.capstone2.fragments.feeds

import com.polyak.iconswitch.IconSwitch

interface IconSwitchListener {
    fun onIconSwitchChanged(state: IconSwitch.Checked)
}