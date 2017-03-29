package com.vaadin.tutorial.eventstagram;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tutorial.eventstagram.backend.OurEvent;
import com.vaadin.tutorial.eventstagram.backend.OurEventService;
import com.vaadin.tutorial.eventstagram.backend.User;
import com.vaadin.tutorial.eventstagram.backend.UserService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;


import com.vaadin.ui.Notification.Type;


import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.TextField;

/* User Interface written in Java.
 *
 * Define the user interface shown on the Vaadin generated web page by extending the UI class.
 * By default, a new UI instance is automatically created when the page is loaded. To reuse
 * the same instance, add @PreserveOnRefresh.
 */

//CHANGES 1


@Title("Eventstagram")
@Theme("valo")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class EventstagramUI extends UI {
	
	private static final long serialVersionUID = 1L;
	protected boolean showingProfilePage = false;
	protected boolean showingLoginForm = false;
	protected boolean showingLoginButton = true;
	protected boolean eventEditing = false;

    /*
     * Hundreds of widgets. Vaadin's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default Vaadin components are in the
     * com.vaadin.ui package and there are over 500 more in
     * vaadin.com/directory.
     */
	
	User currentUser = new User();
	
    TextField filter = new TextField();
    Grid eventList = new Grid();
    Button newEvent = new Button("New Event");
    
    Button profilePageButton = new Button("Profile Page");
    Button loginButton = new Button("Login");
    Button logoutButton = new Button("Logout");
    Button manageLocationsButton = new Button("Manage Locations");

    // EventForm is an example of a custom component class
  
    EventForm eventForm = new EventForm();
    LoginForm loginForm = new LoginForm();
    
    ProfilePageUI profilePageUI = new ProfilePageUI();

    // ContactService is a in-memory mock DAO that mimics
    // a real-world datasource. Typically implemented for
    // example as EJB or Spring Data based service.
    OurEventService service = OurEventService.createDemoService();
    UserService userService = UserService.createDemoService();

    /*
     * The "Main method".
     *
     * This is the entry point method executed to initialize and configure the
     * visible user interface. Executed on every browser reload because a new
     * instance is created for each web page loaded.
     */
    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        /*
         * Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. Vaadin automatically sends only
         * the needed changes to the web page without loading a new page.
         */
        newEvent.addClickListener(e -> eventForm.edit(new OurEvent()));
      
        loginButton.addClickListener(e -> openLoginPage());
        logoutButton.setVisible(!showingLoginButton);       //Set the visibility of the logout button opposite of the login button
        logoutButton.addClickListener(e -> logout()); 		//Add the action to the logout button
        profilePageButton.setVisible(!showingLoginButton);  //Set the visibility of the profile button opposite of the login button
        newEvent.setVisible(!showingLoginButton); 			//Set the visibility of the new event button opposite of the login button
        manageLocationsButton.setVisible(false); 			//Set the initial visibility to false
        
        profilePageButton.addClickListener(e -> openProfilePage());

        filter.setInputPrompt("Filter Events...");
//        filter.setPlaceholder("Filter Events...");
        filter.addTextChangeListener(e -> refreshEvents(e.getText()));
//        filter.addValueChangeListener(e -> refreshEvents(e.getValue()));
       
//        eventList.setWidth("80%");
        eventList.setContainerDataSource(new BeanItemContainer<>(OurEvent.class));
        eventList.setColumnOrder("title", "description", "start", "end", "open");
        eventList.getColumn("description").setWidth(400);
        eventList.getColumn("open").setHeaderCaption("Doors Open");
        eventList.removeColumn("id");
        eventList.removeColumn("locationId");
        eventList.removeColumn("releaseDate");
        eventList.setSelectionMode(Grid.SelectionMode.SINGLE);

        eventList.addSelectionListener(e -> eventForm.display((OurEvent) eventList.getSelectedRow(), !showingLoginButton));
        eventForm.setWidth("100%");
        refreshEvents();
    }

    /*
     * Robust layouts.
     *
     * Layouts are components that contain other components. HorizontalLayout
     * contains TextField and Button. It is wrapped with a Grid into
     * VerticalLayout for the left side of the screen. Allow user to resize the
     * components with a SplitPanel.
     *
     * In addition to programmatically building layout in Java, you may also
     * choose to setup layout declaratively with Vaadin Designer, CSS and HTML.
     */
    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newEvent, manageLocationsButton, profilePageButton,loginButton, logoutButton);
        
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, eventForm, eventList);
        left.setSizeFull();
        eventList.setSizeFull();
        left.setExpandRatio(eventList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, profilePageUI, loginForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        setContent(mainLayout);
    }

    /*
     * Choose the design patterns you like.
     *
     * It is good practice to have separate data access methods that handle the
     * back-end access and/or the user interface updates. You can further split
     * your code into classes to easier maintenance. With Vaadin you can follow
     * MVC, MVP or any other design pattern you choose.
     */
    void refreshEvents() {
        refreshEvents(filter.getValue());
    }
    

    private void refreshEvents(String stringFilter) {
/*        eventList.setContainerDataSource(new BeanItemContainer<>(
        		Event.class, eventservice.findAll(stringFilter)));
*/
        eventList.setContainerDataSource(new BeanItemContainer<>(
                OurEvent.class, service.findAll(stringFilter)));
    	eventForm.setVisible(false);
        profilePageUI.setVisible(false);
        loginForm.setVisible(showingLoginForm);
    }
    
    /**!
     * @brief openProfilePage
     */
    private void openProfilePage()
    {
    	showingProfilePage = !showingProfilePage;
    	profilePageUI.setVisible(showingProfilePage);
    }
    private void openLoginPage()
    {
    	showingLoginForm = !showingLoginForm;
    	
    	loginForm.setVisible(showingLoginForm);
    }
    private void logout(){
    	showingLoginButton=true;							//Change login button state
    	Notification.show("Goodbye " + profilePageUI.userNameContent.getValue() + ".", Type.TRAY_NOTIFICATION); //goodbye message
    	loginForm.clearLoginForm();							//Clear the password and username from the login form
    	loginButton.setVisible(showingLoginButton);			//Show login button
    	logoutButton.setVisible(!showingLoginButton);		//Hide logout button
    	profilePageButton.setVisible(!showingLoginButton);	//Hide profile button
        profilePageUI.userNameContent.setValue("");			//Clear the username from the profile page
        profilePageUI.setVisible(!showingLoginButton); 		//Hide the profile page if showing.
        newEvent.setVisible(!showingLoginButton);   		//Hide the newEvent button
        eventForm.setVisible(false); 						//Hide the event form on logout
        manageLocationsButton.setVisible(false); 			//Hide the manage Locations buttons on logout

    }

    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = EventstagramUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8450898083951649350L;
    }

}
