package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dao.UserDAO;
import model.User;

@WebServlet (urlPatterns = "/upload-avatar")
public class UploadImage  extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7530725717695469508L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		int id = (int) session.getAttribute("id");
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		try {
			List<FileItem> fileItems = upload.parseRequest(req);
			for (FileItem fileItem : fileItems) {
				if (!fileItem.isFormField()) {
					String nameimg = fileItem.getName();
					if (!nameimg.equals("")) {
						String dirUrl = getServletContext().getRealPath("") + File.separator + "avatar";
						File dir = new File(dirUrl);
						if (!dir.exists()) {
							dir.mkdir();
						}
						String fileImg = dirUrl + File.separator + id + ".png";
						File file = new File(fileImg);
						try {
						    if (file.exists()) {
						    	file.delete();
						    }
							fileItem.write(file);
							User user = User.builder().profile(id + ".png").id(id).build();
							UserDAO.update(user);
							session.setAttribute("uploadAvatar", true);
						} catch (Exception e) {
							session.setAttribute("uploadAvatar", false);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (FileUploadException e) {
			session.setAttribute("uploadAvatar", false);
			e.printStackTrace();
		}
		resp.sendRedirect(req.getContextPath() + "/setting");
	}
}
