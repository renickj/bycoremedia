package com.coremedia.livecontext.asset;

import com.coremedia.cap.content.Content;

import java.util.List;

public interface AssetValidationService {
  List<Content> filterAssets(List<Content> source);
}
