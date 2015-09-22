package com.gnts.erputil.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.springframework.context.annotation.Scope;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@Scope("request")
public class UploadDocumentUI implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Embedded embedded;
	private String basepath;
	private String basepath1;
	private String basepath2;
	private VerticalLayout imgPanel = new VerticalLayout();
	Label lblFileName = new Label();
	
	public UploadDocumentUI(VerticalLayout layout) {
		basic(layout);
	}
	
	private void basic(VerticalLayout layout) {
		layout.removeAllComponents();
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath1 = basepath + "/VAADIN/themes/gerp/img/Document.pdf";
		// BEGIN-EXAMPLE: component.upload.basic
		// Create the upload with a caption and set receiver later
		Upload upload = new Upload("", null);
		upload.setStyleName("myCustomUpload");
		upload.setButtonCaption("Attach File");
		upload.setImmediate(true);
		Upload upp = new Upload();
		upp.setButtonCaption("Download");
		VerticalLayout panel = new VerticalLayout();
		panel.removeAllComponents();
		panel.addComponent(upload);
		class ImageUploader implements Receiver, SucceededListener {
			private static final long serialVersionUID = -1276759102490466761L;
			public File file;
			
			// /imgPanel.removeAllComponents();
			public OutputStream receiveUpload(String filename, String mimeType) {
				// Create upload stream
				FileOutputStream fos = null; // Output stream to write to
				basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
				basepath1 = basepath + "/VAADIN/themes/gerp/img/Document.pdf";
				try {
					// Open the file for writing.
					file = new File(basepath1);
					fos = new FileOutputStream(file);
				}
				catch (final java.io.FileNotFoundException e) {
					Notification.show("Could not open file<br/>", e.getMessage(), Type.ERROR_MESSAGE);
					return null;
				}
				return fos; // Return the output stream to write to
			}
			
			@SuppressWarnings("deprecation")
			public void uploadSucceeded(SucceededEvent event) {
				// Show the uploaded file in the image viewer
				try {
					File f = new File(event.getFilename());
					String name = f.getName();
					@SuppressWarnings("resource")
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					try {
						for (int readNum; (readNum = fis.read(buf)) != -1;) {
							bos.write(buf, 0, readNum); // no doubt here is 0
						}
					}
					catch (Exception ex) {
					}
					UI.getCurrent().getSession().setAttribute("UPLOAD_FILE_NAME", event.getFilename());
					UI.getCurrent().getSession().setAttribute("UPLOAD_FILE_BYTE", bos.toByteArray());
					UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", true);
					UI.getCurrent().getSession().setAttribute("uploadedFilePath", f.getAbsolutePath());
					UI.getCurrent().getSession().setAttribute("uploadedDocumentName", name);
					lblFileName.setValue(name);
					imgPanel.removeAllComponents();
					FileResource fRes = new FileResource(file);
					fRes.setCacheTime(10);
					embedded = new Embedded(null, fRes);
					embedded.setType(Embedded.TYPE_BROWSER);
					embedded.setMimeType("application/pdf");
					embedded.setParameter("allowFullScreen", "true");
					embedded.setWidth("320");
					embedded.setHeight("150");
					imgPanel.addComponent(embedded);
					imgPanel.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);
					imgPanel.setWidth("320");
					imgPanel.setHeight("150");
				}
				catch (Exception e) {
					Notification.show("", " File Format is incorrect", Type.ERROR_MESSAGE);
				}
			}
		}
		;
		final ImageUploader uploader = new ImageUploader();
		upload.setReceiver(uploader);
		upload.addSucceededListener(uploader);
		// END-EXAMPLE: component.upload.basic
		// Create uploads directory
		basepath2 = basepath + "/VAADIN/themes/gerp/img/Document.pdf";
		File uploads = new File(basepath2);
		if (!uploads.exists() && !uploads.mkdir()) layout.addComponent(new Label("ERROR: Could not create upload dir"));
		memorybuffer(layout);
		layout.addComponent(imgPanel);
		layout.addComponent(panel);
		layout.setSpacing(true);
	}
	
	void memorybuffer(VerticalLayout layout) {
		// BEGIN-EXAMPLE: component.upload.memorybuffer
		// Create the upload with a caption and set receiver later
		Upload upload = new Upload("Upload the Document here", null);
		final ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
		// Implement receiver that stores in the memory buffer
		class ImageReceiver implements Receiver {
			private static final long serialVersionUID = -1276759102490466761L;
			
			public OutputStream receiveUpload(String filename, String mimeType) {
				os.reset(); // If re-uploading
				return os;
			}
		}
		;
		final ImageReceiver receiver = new ImageReceiver();
		upload.setReceiver(receiver);
	}
	
	@SuppressWarnings("deprecation")
	public void displaycertificate(byte[] certificate) {
		imgPanel.removeAllComponents();
		if (certificate != null && !"null".equals(certificate)) {
			UI.getCurrent().getSession().setAttribute("UPLOAD_FILE_NAME", certificate);
			UI.getCurrent().getSession().setAttribute("UPLOAD_FILE_BYTE", "Document.pdf");
			UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", true);
			File someFile = new File(basepath1);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(someFile);
				fos.write(certificate);
				fos.flush();
				fos.close();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			FileResource filere = new FileResource(someFile);
			filere.setCacheTime(10);
			embedded = new Embedded(null, filere);
			embedded.setType(Embedded.TYPE_BROWSER);
			embedded.setMimeType("application/pdf");
			embedded.setParameter("allowFullScreen", "true");
			embedded.setWidth("320");
			embedded.setHeight("150");
			imgPanel.addComponent(embedded);
			imgPanel.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);
			imgPanel.setWidth("320");
			imgPanel.setHeight("150");
			imgPanel.setSpacing(true);
		}
	}
}