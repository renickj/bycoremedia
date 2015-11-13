package com.coremedia.blueprint.studio.util {

import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.user.Group;
import com.coremedia.cap.user.User;
import com.coremedia.ui.data.ValueExpressionFactory;

/**
 * Common user utility methods.
 */
public class UserUtil {

  private static var groups:Array = [];
  private static var userName:String;
  private static var home:Content;

  /**
   * Is invoked during the startup of the plugin.
   * It caches all direct groups of the current user group recursivly.
   */
  public static function init():void {
    var user:User = session.getUser();
    user.load(function ():void {
      userName = user.getName();
      home = user.getHomeFolder();
      home.load(function ():void {
        ValueExpressionFactory.create(ContentPropertyNames.PATH, home).loadValue(function ():void {
          //check the direct groups of the user for any match.
          var groups:Array = user.getDirectGroups();
          for (var j:int = 0; j < groups.length; j++) {
            var directGroup:Group = groups[j];
            loadGroup(directGroup);
          }
        });
      });
    });
  }

  /**
   * Returns the home folder of the active user.
   * @return
   */
  public static function getHome():Content {
    return home;
  }

  /**
   * Recursive call for group loading.
   * @param group The group to load, including it's sub-groups.
   */
  private static function loadGroup(group:Group):void {
    group.load(function ():void {
      groups.push(group);
      var directGroups:Array = group.getDirectGroups();
      directGroups.forEach(function (dGroup:Group):void {
        loadGroup(dGroup);
      });
    });
  }

  /**
   * Checks if the user is in the group with the given name.
   * Since we load the group name recursively, it can be a parent group too.
   * @param group The name of the group to check
   * @param domain Optional, the domain can also be passed via concatenated '@' symbol.
   * @return True, if the active user is member of the given group.
   */
  public static function isInGroup(groupName:String, domainName:String = undefined):Boolean {
    var name:String = groupName;
    var domain:String = domainName;
    if (groupName.indexOf("@") > 0) {
      name = groupName.substr(0, groupName.lastIndexOf("@"));
      domain = groupName.substr(groupName.lastIndexOf("@") + 1, groupName.length);
    }

    for (var i:int = 0; i < groups.length; i++) {
      if (groups[i].getName() === name) {
        if (!domain) { //soft check
          return true;
        }
        if (groups[i].getDomain() === domain) {
          return true;
        }
      }
    }
    return false;
  }
}
}