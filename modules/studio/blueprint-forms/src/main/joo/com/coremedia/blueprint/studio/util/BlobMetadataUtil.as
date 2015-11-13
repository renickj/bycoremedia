package com.coremedia.blueprint.studio.util {

import com.coremedia.ui.data.impl.RemoteService;

import ext.Ext;
import ext.XTemplate;
import ext.tree.AsyncTreeNode;
import ext.tree.TreeNode;
import ext.util.Format;

/**
 * Common utility method for the studio.
 */
public class BlobMetadataUtil {

  public static function viewFileHandler(treeNode:*):void {
    if (treeNode && treeNode.attributes && treeNode.attributes.url) {
      var url:String = RemoteService.calculateRequestURI(treeNode.attributes.url);
      window.open(url, treeNode.attributes.path || treeNode.attributes.name || '');
    }
  }

  public static function convertDirectoryTree(files:Array):TreeNode {
    return new AsyncTreeNode({
      expanded:true,
      leaf:false,
      text:"",
      children:convertChildren(files)
    });
  }

  private static function convertTree(file:*):* {
    // Note: be aware to avoid name conflicts with Ext Tree properties, e.g. 'leaf' and 'children', 'loader' etc. !
    var attributes:* = Ext.apply({
      leaf:!file.directory
    }, {}, file);
    if (file.directory) {
      attributes.children = convertChildren(file.children);
    }
    return attributes;
  }

  public static function formatSize(size:Number, record:*):String {
    return record.directory ? '' : Format.fileSize(size);
  }

  private static function convertChildren(files:Array):Array {
    var result:Array = [];
    files.forEach(function (file:*):void {
      result.push(convertTree(file));
    });
    return result;
  }

  public static function emptyRootNode():TreeNode {
    var treeNode:TreeNode = new AsyncTreeNode({
      expanded:true,
      leaf:false,
      text:"",
      children:[]
    });
    var superRenderChildren:Function = treeNode['renderChildren'];
    treeNode['renderChildren'] = function ():void {
      this.childNodes = [];
      superRenderChildren.apply(this, arguments);
    };
    return treeNode;
  }

  public static function fileNameTemplate():* {
    return new XTemplate('<a href="{url}" target="_blank">{name:htmlEncode}</a>');
  }

  public static function fileSizeTemplate():* {
    return new XTemplate('{size:this.fmtSize}', {fmtSize:BlobMetadataUtil.formatSize});
  }

  public static function filesAvailable(md:*):Boolean {
    return md && md.archive && md.archive.files;
  }
}
}
