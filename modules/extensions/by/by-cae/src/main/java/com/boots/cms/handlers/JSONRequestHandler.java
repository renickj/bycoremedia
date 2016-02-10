package com.boots.cms.handlers;

import com.boots.cms.handlers.MyBean;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.search.SearchResult;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.content.Content;

import org.springframework.beans.factory.annotation.Required;
import com.coremedia.cap.content.search.SearchService;

import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.coremedia.xml.Markup;

@RequestMapping
public class JSONRequestHandler{
	
	private ContentRepository contentRepository;
	
	
	@Required
	public void setContentRepository(ContentRepository contentRepository) {
	    this.contentRepository = contentRepository;
	}
	@RequestMapping(value = "/search/{term}",method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public MyBean searchArticle(@PathVariable("term") String term) {
		SearchService ss = contentRepository.getSearchService();
		SearchResult result = ss.search(term);
		List<Content> hits = result.getMatches();
		System.out.println("Contents---->"+hits.toString());
		MyBean myBean = new MyBean();
		myBean.setId(term);
		myBean.setTitle(hits.toString());
		
	 return myBean;
	}
	
	@RequestMapping(value = "/json/{id}",method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public MyBean renderBeanAsJson(@PathVariable("id") String id) {
		System.out.println("Search Term-->"+id);
		QueryService queryService = contentRepository.getQueryService();
		//Content content = queryService.getContentFulfilling("TYPE CMArticle: NOT isDeleted");
		String Query1 = "TYPE BYArticle: NOT isDeleted AND (title CONTAINS '"+id+"' OR productInfo CONTAINS '"+id+"' OR detailText CONTAINS '"+id+"')";
		System.out.println("Query--->"+Query1);
		Collection<Content> res = queryService.poseContentQuery(Query1);
		System.out.println("Collection<Content>-->"+res.toString());
		String vString = "";
		for(Content c : res){
			System.out.println("Contents-->"+c.toString());
			Content videoCon = c.getLink("video");
			if(videoCon!=null){
			System.out.println("Video Contents-->"+videoCon.toString());
			}
			Markup productInfo = c.getMarkup("productInfo");
			if(productInfo!=null){
			System.out.println("Markup Contents-->"+productInfo.toString());
			vString = productInfo.toString();
			}
			String title = c.getString("title");
			if(title!=null){
				System.out.println("title-->"+title);
				vString = title;
			}
			List<Content> tagsCon = c.getLinks("subjectTaxonomy");
			if(tagsCon!=null){
				System.out.println("tagsCon"+tagsCon.toString());
			}
			
			/*vString = getJsonString(videoCon);
			System.out.println("vString-->"+vString);
			String tString = getJsonString(productInfo);
			System.out.println("tString-->"+tString);*/
		}
		//String retJson = new Gson().toJson(res);
		//System.out.println("retJson-->"+retJson);
		//Content content = result.get(0);
		MyBean myBean = new MyBean();
		myBean.setId(id);
		myBean.setTitle(vString);
		
	 return myBean;
	}
	
	@RequestMapping(value="/post/{id}", method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	public MyBean1 renderBeanAsJsonForPost(@PathVariable("id") String id, @RequestBody MyBean bean) {
		System.out.println("Post input--->"+bean.getId());
		MyBean1 bean1 = new MyBean1();
		bean1.setId(id);
		return bean1;
	}
	
	private String getJsonString(Object o){
		return new Gson().toJson(o);
	}
	
}
