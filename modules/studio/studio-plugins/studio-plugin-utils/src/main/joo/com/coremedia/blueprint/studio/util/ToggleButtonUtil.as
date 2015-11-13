package com.coremedia.blueprint.studio.util {
import ext.Button;

public class ToggleButtonUtil {
  public static const TOGGLEBTN_INFO_CLASS:String = 'issue-info';
  public static const TOGGLEBTN_PRESSED_CLASS:String = 'x-btn-pressed';

  public static function setPressed(btn:Button):void {
    btn.addClass(TOGGLEBTN_INFO_CLASS);
    btn.addClass(TOGGLEBTN_PRESSED_CLASS);
  }

  public static function setUnpressed(btn:Button):void {
    btn.removeClass(TOGGLEBTN_INFO_CLASS);
    btn.removeClass(TOGGLEBTN_PRESSED_CLASS);
  }
}
}