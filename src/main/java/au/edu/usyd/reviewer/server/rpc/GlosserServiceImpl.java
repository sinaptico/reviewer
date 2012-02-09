package au.edu.usyd.reviewer.server.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.cfg.AnnotationConfiguration;

import au.edu.usyd.glosser.app.site.Site;
import au.edu.usyd.glosser.app.site.SiteDao;
import au.edu.usyd.glosser.app.site.SiteMessage;
import au.edu.usyd.reviewer.client.admin.glosser.GlosserService;
import au.edu.usyd.reviewer.client.admin.glosser.SiteException;
import au.edu.usyd.reviewer.client.admin.glosser.SiteForm;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GlosserServiceImpl extends RemoteServiceServlet implements GlosserService {

	private static final long serialVersionUID = 1L;
	private SiteDao siteDao = new SiteDao();

	public GlosserServiceImpl() {
		super();
		siteDao = new SiteDao();
		siteDao.setSessionFactory(new AnnotationConfiguration().configure("glosser-hibernate.cfg.xml").buildSessionFactory());
	}

	@Override
	public void deleteSites(List<SiteForm> siteForms) {
		List<Site> sites = new LinkedList<Site>();
		for (SiteForm siteForm : siteForms) {
			Site site = siteDao.getSite(siteForm.getId());
			sites.add(site);
		}
		siteDao.deleteSites(sites);
	}

	@Override
	public List<SiteForm> getAllSites() {
		List<Site> sites = siteDao.getAllSites();
		List<SiteForm> res = new LinkedList<SiteForm>();
		for (Site site : sites) {
			res.add(siteToSiteFrom(site));
		}
		return res;
	}

	@Override
	public List<String> getToolList() {
		List<String> l = new LinkedList<String>();
		l.add("home");
		l.add("structure");
		l.add("flow");
		l.add("flowmap");
		l.add("topics");
		l.add("topicsmap");
		l.add("conceptmap");
		l.add("question");
		l.add("language");
		l.add("participation");
		return l;
	}

	@Override
	public void saveOrUpdateSite(SiteForm siteForm) {
		Site site = siteDao.getSite(siteForm.getId());
		try {
			HashMap<String, String> fieldANDvalue = siteForm.getFieldANDvalue();
			Set<String> fields = fieldANDvalue.keySet();
			for (String field : fields) {
				if ("messages".equalsIgnoreCase(field)) {
					List<SiteMessage> messages = site.getMessages();
					Map<String, String> Form_Messages = siteForm.getFormMessages();
					Set<String> keySet = Form_Messages.keySet();
					List<SiteMessage> result = new LinkedList<SiteMessage>();
					for (SiteMessage msg : messages) {
						String code = msg.getCode();
						if (keySet.contains(code)) {
							msg.setValue(Form_Messages.get(code));
							Form_Messages.remove(code);
							result.add(msg);
						} else {
							// msg will be deleted
						}
					}
					for (String msgKey : Form_Messages.keySet()) {
						SiteMessage sm = new SiteMessage();
						sm.setCode(msgKey);
						sm.setValue(Form_Messages.get(msgKey));
						result.add(sm);
					}

					site.setMessages(result);
				} else if ("tools".equalsIgnoreCase(field)) {
					BeanUtils.copyProperty(site, "tools", siteForm.getFormTools());
				} else {
					BeanUtils.copyProperty(site, field, fieldANDvalue.get(field));
				}
			}

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		siteDao.saveOrUpdate(site);
	}

	@Override
	public SiteForm saveSite() throws SiteException {
		Site tmp = new Site();
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/site-default.properties"));
		} catch (Exception e) {
			throw new SiteException("Failed to add site. Cannot load default messages.");
		}
		Enumeration<String> elements = (Enumeration<String>) prop.propertyNames();
		List<SiteMessage> siteMessage = new LinkedList<SiteMessage>();
		while (elements.hasMoreElements()) {
			SiteMessage sm = new SiteMessage();
			String nextElement = elements.nextElement();
			sm.setCode(nextElement.toString());
			sm.setValue(prop.getProperty(nextElement));
			siteMessage.add(sm);
		}
		tmp.setMessages(siteMessage);
		tmp.setTools(new LinkedList<String>());
		tmp.setName(" ");

		try {
			Site site = siteDao.save(tmp);
			return siteToSiteFrom(site);
		} catch (Exception e) {
			throw new SiteException("Failed to add site. Site name may already exist.");
		}
	}

	private SiteForm siteToSiteFrom(Site site) {
		SiteForm siteForm = new SiteForm();
		try {
			BeanUtils.copyProperties(siteForm, site);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// sf.setSite_id(site.getId());

		List<String> tools = site.getTools();
		List<String> toolRes = new LinkedList<String>();

		for (String tool : tools) {
			toolRes.add(tool);
		}
		siteForm.setFormTools(toolRes);

		List<SiteMessage> messages = site.getMessages();
		Map<String, String> messagesRes = new HashMap<String, String>();
		for (SiteMessage msg : messages) {
			messagesRes.put(msg.getCode(), msg.getValue());
		}
		siteForm.setFormMessages(messagesRes);
		
		return siteForm;
	}

}
