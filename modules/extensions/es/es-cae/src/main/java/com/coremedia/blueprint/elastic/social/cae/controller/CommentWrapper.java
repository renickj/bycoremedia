package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.elastic.social.api.comments.Comment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CommentWrapper extends ContributionWrapper<Comment, CommentWrapper> {

  public CommentWrapper(@Nonnull Comment comment, @Nullable List<CommentWrapper> subComments) {
    super(comment, subComments);
  }

  public Comment getComment() {
    return super.getContribution();
  }

  public void setComment(Comment comment) {
    super.setContribution(comment);
  }

  public List<CommentWrapper> getSubComments() {
    return super.getSubContributions();
  }
}
