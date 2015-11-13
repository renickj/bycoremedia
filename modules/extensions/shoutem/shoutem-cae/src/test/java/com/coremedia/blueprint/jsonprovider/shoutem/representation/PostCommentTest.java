package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.jsonprovider.shoutem.ShoutemApiCredentials;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.models.ModelException;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostCommentTest {

  @Mock
  private ShoutemApiCredentials credentials;

  @Mock
  private Comment comment;

  @Mock
  private CommunityUser user;


  @Mock
  private Blob blob;

  @Test
  public void commentWithAuthorAndImage() {
    String commentId = "1111";
    String text = "hello world";
    String author = "nick";
    String authorId = "4711";
    String blobId = "222";
    String expectedBlobId = "elastic/image/222/50/50";
    when(comment.getAuthor()).thenReturn(user);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(text);
    when(user.getId()).thenReturn(authorId);
    when(user.getImage()).thenReturn(blob);
    when(user.getName()).thenReturn(author);
    when(user.isAnonymous()).thenReturn(false);
    when(blob.getId()).thenReturn(blobId);
    when(credentials.getUser()).thenReturn(user);

    PostComment postComment = new PostComment(credentials, comment, "true");
    assertNotNull(postComment);
    assertEquals(text, postComment.getMessage());
    assertEquals(commentId, postComment.getComment_id());
    assertEquals(author, postComment.getAuthor());
    assertEquals(authorId, postComment.getAuthor_id());
    assertTrue(postComment.getAuthor_image_url().contains(expectedBlobId));
  }

  @Test
  public void commentWithAnonymousAuthor() {
    String commentId = "1111";
    String text = "hello world";
    String author = "nick";
    String authorId = "4711";
    when(comment.getAuthorName()).thenReturn(author);
    when(comment.getAuthor()).thenReturn(user);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(text);
    when(user.getId()).thenReturn(authorId);
    when(user.isAnonymous()).thenReturn(true);
    when(credentials.getUser()).thenReturn(user);

    PostComment postComment = new PostComment(credentials, comment, "true");
    assertNotNull(postComment);
    assertEquals(text, postComment.getMessage());
    assertEquals(commentId, postComment.getComment_id());
    assertEquals(author, postComment.getAuthor());
    assertEquals(authorId, postComment.getAuthor_id());
  }

  @Test
  public void testCommentsAuthorNull() {
    String commentId = "1111";
    String text = "hello world";
    when(comment.getAuthor()).thenReturn(null);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(text);
    PostComment postComment = new PostComment(credentials, comment, "true");
    assertNotNull(postComment);
    assertEquals(text, postComment.getMessage());
    assertEquals(commentId, postComment.getComment_id());
    assertEquals("anonymous", postComment.getAuthor());
    assertEquals("", postComment.getAuthor_id());
  }

  @Test
  public void testCommentsAuthorInvalid() {
    String commentId = "1111";
    String text = "hello world";
    when(comment.getAuthor()).thenReturn(user);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(text);
    when(user.getId()).thenReturn("12");
    when(user.isAnonymous()).thenThrow(new ModelException("no delegate for model"));
    when(credentials.getUser()).thenReturn(user);
    PostComment postComment = new PostComment(credentials, comment, "true");
    assertNotNull(postComment);
    assertEquals(text, postComment.getMessage());
    assertEquals(commentId, postComment.getComment_id());
    assertEquals("anonymous", postComment.getAuthor());
    assertEquals("12", postComment.getAuthor_id());
  }

  @Test
  public void testCommentsAuthorImageNull() {
    String commentId = "1111";
    String text = "hello world";
    String author = "nick";
    String authorId = "4711";
    when(comment.getAuthor()).thenReturn(user);
    when(comment.getId()).thenReturn(commentId);
    when(comment.getText()).thenReturn(text);
    when(user.getId()).thenReturn(authorId);
    when(user.getImage()).thenReturn(null);
    when(user.getName()).thenReturn(author);
    when(user.isAnonymous()).thenReturn(false);
    when(credentials.getUser()).thenReturn(user);
    PostComment postComment = new PostComment(credentials, comment, "true");
    assertNotNull(postComment);
    assertEquals(text, postComment.getMessage());
    assertEquals(commentId, postComment.getComment_id());
    assertEquals(author, postComment.getAuthor());
    assertEquals(authorId, postComment.getAuthor_id());
  }
}