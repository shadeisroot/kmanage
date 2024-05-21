package org.example.kmanage.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.kmanage.DAO.PlistDAO;
import org.example.kmanage.DAO.ProfileDAO;
import org.example.kmanage.Main;
import org.example.kmanage.User.Profile;
import org.example.kmanage.User.Project;
import org.example.kmanage.User.User;
import org.example.kmanage.User.UserSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ControllerTest {
    //mocks er simuleringer at objekter og InjectMock simulere den med flere mock objekter
    // så det kan testes uafhængigt af hinanden
    @Mock
    private PlistDAO plistDAO;
    @Mock
    private ProfileDAO edi;
    @Mock
    private UserSession userSession;

    @Mock
    private Main main;

    @InjectMocks
    private HelloController controller;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });
        latch.await();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = spy(new HelloController());
        controller.plist = new TableView<>();
        controller.personSearchField = new TextField();
        controller.calendarGrid = new GridPane();

    }

    @Test
    void getCurrentDate() {
        // den dato den siger i controlleren
        LocalDate actualDate = controller.getCurrentDate();
        //den dag vi forventer (i dag)
        LocalDate expectedDate = LocalDate.now();
        //om forventet dag er = den dag i controller
        assertEquals(expectedDate, actualDate);
    }

    @Test
    void getProjects() {
        //opretter 2 projekter, med nogle værdier og sætter den i observable liste
        Project projekt1 = new Project("Projekt1", LocalDate.now(), LocalDate.now().plusDays(1), 1, "Note1", LocalDate.now(), Arrays.asList(LocalDate.now()));
        Project projekt2 = new Project("Projekt2", LocalDate.now().plusDays(2), LocalDate.now().plusDays(3), 2, "Note2", LocalDate.now().plusDays(2), Arrays.asList(LocalDate.now().plusDays(2)));

        ObservableList<Project> expectedProjects = FXCollections.observableArrayList(projekt1, projekt2);

        //opretter listen med projekter fra controller
        controller.getProjects().addAll(expectedProjects);
        ObservableList<Project> actualProjects = controller.getProjects();

        //sammenligner de to observable lister
        assertEquals(expectedProjects, actualProjects);

    }


    @Test
    void filterplist() {
        //laver 2 profiler og ind i liste
        Profile profile1 = new Profile("Dennis", "Elev", "Sjælland", 1);
        Profile profile2 = new Profile("Jacob", "Elev", "Jylland", 2);
        ObservableList<Profile> profiles = FXCollections.observableArrayList(profile1, profile2);

        controller.plist.setItems(profiles);
        //setter listen to den som er i controller så den bruge filterplist og kalder den
        controller.profiles = profiles;

        controller.filterplist();

        // simulere det der skrives i søgefeltet og opdatere listen (både en for Dennis og en for Jacob)
        controller.personSearchField.setText("Dennis");
        controller.personSearchField.fireEvent(new javafx.event.Event(javafx.scene.input.InputEvent.ANY));
        //true hvis profil1 vises
        assertTrue(controller.plist.getItems().contains(profile1));
        assertFalse(controller.plist.getItems().contains(profile2));

        controller.personSearchField.setText("Jacob");
        controller.personSearchField.fireEvent(new javafx.event.Event(javafx.scene.input.InputEvent.ANY));
        //true hvis profile2 vises
        assertFalse(controller.plist.getItems().contains(profile1));
        assertTrue(controller.plist.getItems().contains(profile2));
    }

    @Test
    void updateCalender() {
        LocalDate newDate = LocalDate.now().plusDays(5);

        // laver mocks til dayView, weekView, and monthView
        doNothing().when(controller).dayView();
        doNothing().when(controller).weekView();
        doNothing().when(controller).monthView();

        // Test for ViewMode.DAG ved at sætte den til dag
        controller.currentViewMode = HelloController.ViewMode.DAG;
        //opdatere kalenderen
        controller.updateCalender(newDate);
        //tjekker om den nye dag er i den som passer til controller
        assertEquals(newDate, controller.getCurrentDate());
        //bekræfter det kun er dayview, som bliver kaldt en gang og at de andre ikke bliver kaldt
        verify(controller, times(1)).dayView();
        verify(controller, never()).weekView();
        verify(controller, never()).monthView();

        // Test for ViewMode.UGE uden bekræftelse
        controller.currentViewMode = HelloController.ViewMode.UGE;
        controller.updateCalender(newDate);
        assertEquals(newDate, controller.getCurrentDate());
;

        // Test for ViewMode.MÅNED uden bekræftelse
        controller.currentViewMode = HelloController.ViewMode.MÅNED;
        controller.updateCalender(newDate);
        assertEquals(newDate, controller.getCurrentDate());

    }

    @Test
    void zoomOutPressed() {
        //mocker update kalender metoden
        doNothing().when(controller).updateCalender(any(LocalDate.class));

        // mocker getCurrentDate metoden
        LocalDate mockDate = LocalDate.now();
        doReturn(mockDate).when(controller).getCurrentDate();

        // Simulere en ActionEvent
        ActionEvent mockEvent = mock(ActionEvent.class);

        // Setter currentViewMode til at vise uge
        controller.currentViewMode = HelloController.ViewMode.UGE;

        // kalder dens mock event
        controller.zoomOutPressed(mockEvent);

        // tjekker om currentView nu er måned
        assertEquals(HelloController.ViewMode.MÅNED, controller.currentViewMode);

    }


    @Test
    void todayButtonPressed() {
        //opdatere kalender metoden
        doNothing().when(controller).updateCalender(any(LocalDate.class));

        // Simulere event
        ActionEvent mockEvent = mock(ActionEvent.class);

        // kalder eventete
        controller.todayButtonPressed(mockEvent);

        // kigger om currentDate er blevet sat til i dag
        LocalDate today = LocalDate.now();
        assertEquals(today, controller.getCurrentDate());

    }
}