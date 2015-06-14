package com.gnts.erputil.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import com.vaadin.server.FileResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@Scope("request")
public class UploadUI {
	private static Logger logger = Logger.getLogger(UploadUI.class);
	private VerticalLayout vlimage = new VerticalLayout();
	public String imageFilePath = "", basepath = "";
	private Image imageFrame = new Image(null);
	Upload upload;
	
	public UploadUI(final HorizontalLayout layout) {
		basic(layout);
	}
	
	private void basic(HorizontalLayout layout) {
		logger.info("upload ui class called");
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
		layout.removeAllComponents();
		Cookie cookie = new Cookie("imageFilePath", imageFilePath);
		// Use a fixed path
		cookie.setPath("/localhost:8080/gerp-web/");
		cookie.setMaxAge(10); // One hour
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		imageFilePath = basepath + "/VAADIN/themes/gerp/img/Upload.jpg";
		upload = new Upload(null, null);
		upload.setImmediate(true);
		upload.setButtonCaption("Upload");
		upload.setImmediate(true);
		upload.setStyleName(Runo.BUTTON_SMALL);
		upload.addSucceededListener(new SucceededListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				// TODO Auto-generated method stub
				System.out.println("test");
				Cookie cookie = new Cookie("imageFilePath", imageFilePath);
				cookie.setPath("/localhost:8080/gerp-web/");
				// cookie.setMaxAge(10); // One hour
			}
		});
		imageFrame.setSource(new ThemeResource("img/insert_image.jpeg"));
		imageFrame.setImmediate(true);
		imageFrame.setWidth(85.0f, Unit.PIXELS);
		imageFrame.setHeight(100.0f, Unit.PIXELS);
		class ImageUploader implements Receiver, SucceededListener {
			private static final long serialVersionUID = -1276759102490466761L;
			public File file;
			
			public OutputStream receiveUpload(String filename, String mimeType) {
				// Create upload stream
				FileOutputStream fos = null; // Output stream to write to
				try {
					// Open the file for writing.
					imageFilePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath()
							+ "/VAADIN/themes/gerp/img/" + filename;
					file = new File(imageFilePath);
					fos = new FileOutputStream(file);
				}
				catch (final java.io.FileNotFoundException e) {
					Notification.show("Could not open file<br/>", e.getMessage(), Type.ERROR_MESSAGE);
					return null;
				}
				return fos; // Return the output stream to write to
			}
			
			public void uploadSucceeded(SucceededEvent event) {
				// Show the uploaded file in the image viewer
				try {
					File f = new File(event.getFilename());
					String name = f.getName();
					int k = name.lastIndexOf(".");
					String ext = null;
					if (k != -1) ext = name.substring(k + 1, name.length());
					if (ext.toLowerCase().equals("png".toLowerCase()) || ext.toLowerCase().equals("jpg".toLowerCase())
							|| ext.toLowerCase().equals("jpeg".toLowerCase())) {
						@SuppressWarnings("resource")
						FileInputStream fis = new FileInputStream(file);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						try {
							for (int readNum; (readNum = fis.read(buf)) != -1;) {
								bos.write(buf, 0, readNum); // no doubt here is 0
								// Writes len bytes from the specified byte array starting at offset off to this byte
								// array output stream.
								// System.out.println("read " + readNum + " bytes,");
							}
						}
						catch (Exception ex) {
						}
						UI.getCurrent().getSession().setAttribute("imagebyte", bos.toByteArray());
						UI.getCurrent().getSession().setAttribute("isFileUploaded", true);
						FileResource fRes = new FileResource(file);
						fRes.setCacheTime(10);
						imageFrame.setSource(fRes);
						imageFrame.setImmediate(true);
					} else if (ext.equals(" ")) {
						Notification.show("", " File Format is incorrect", Type.ERROR_MESSAGE);
					} else {
						Notification.show("", "Only JPEG , JPG , PNG format is allowed . The " + ext
								+ " format is not allowed !!", Type.ERROR_MESSAGE);
					}
				}
				catch (Exception e) {
					logger.info("file formate function ");
					Notification.show("", " File Format is incorrect", Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
		;
		final ImageUploader uploader = new ImageUploader();
		upload.setReceiver(uploader);
		upload.addSucceededListener(uploader);
		// END-EXAMPLE: component.upload.basic
		File uploads = new File(imageFilePath);
		if (!uploads.exists() && !uploads.mkdir()) layout.addComponent(new Label("ERROR: Could not create upload dir"));
		vlimage.addComponent(imageFrame);
		vlimage.addComponent(upload);
		layout.addComponent(vlimage);
		logger.info(" after add panel to layout ");
	}
	
	public void dispayImage(byte[] myimage, String filename) {
		imageFrame.setSource(null);
		imageFrame.setSource(new FileResource(new File(viewImage(myimage, filename))));
		UI.getCurrent().getSession().setAttribute("imagebyte", myimage);
		UI.getCurrent().getSession().setAttribute("isFileUploaded", true);
	}
	
	public String viewImage(byte[] myimage, String name) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/"
				+ name + ".png";
		if (myimage != null && !"null".equals(myimage)) {
			InputStream in = new ByteArrayInputStream(myimage);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ImageIO.write(bImageFromConvert, "png", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				ImageIO.write(bImageFromConvert, "jpg", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				return basepath;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return "img/logo.png";
		}
		return "img/logo.png";
	}
}
