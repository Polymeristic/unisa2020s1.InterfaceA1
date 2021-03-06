package main.note;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Rect;
import java.util.ArrayList;


/**
 * Note object that will control a single instance of a Note
 */
public class Note extends Stage {

    /** List of all notes within the application **/
    private static ArrayList<Note> notes
        = new ArrayList<>();

    /** Default content used when creating a new note **/
    private static final String DEFAULT_CONTENT
        = "";

    /** Default note properties used when creating a new note **/
    private static final Properties DEFAULT_NOTE_PROPERTIES
        = Properties.NONE;

    /** Default dimensions used when Note created **/
    private static final Rect DEFAULT_DIMENSIONS
        = new Rect(25, 25, 250, 250);

    /** Padding between new notes **/
    private static final int NEW_NOTE_PADDING
        = 6;

    /** Position of the note **/
    private Rect _dimensions;

    /** ID for this note **/
    private int _noteID;

    /** Contents of the note **/
    private String _content;

    /** Special properties of the note **/
    private Properties _properties;

    /** Stage of this note **/
    private Stage _stage;

    /** Scene of this note **/
    private Scene _scene;

    /** Overall app pane that contains the entire application **/
    private StackPane _appPane;

    /** Pane that contains the controls of the note **/
    private BorderPane _controlPane;

    /** Pane that contains the content of the note **/
    private BorderPane _contentPane;

    /** Controls associated with the Note **/
    private Controls _controls;

    /** Button right tile pane **/
    private TilePane _buttonRightTile;

    /** Color of the note **/
    private ThemeColor _color;

    /**
     * Creates a new Note object
     * @param dimensions    Dimensions of the note
     * @param content       Content of the note
     * @param properties    Special properties for this note
     * @param color         Color of the note
     */
    public Note(Rect dimensions, String content, Properties properties, ThemeColor color) {
        _dimensions = dimensions;
        _content = content;
        _properties = properties;
        _color = color;

        SetUniqueNoteID();
        CreateStage();

        notes.add(this);
    }

    /**
     * Creates a new Note object with empty content
     * @param dimensions Dimensions of the Note
     * @param properties Properties of the Note
     */
    public Note(Rect dimensions, Properties properties) {
        this(dimensions, DEFAULT_CONTENT, properties, ThemeControls.DEFAULT_COLOR);
    }

    /**
     * Creates a new Note object with empty content and default properties
     * @param dimensions Dimensions of the Note
     */
    public Note(Rect dimensions) {
        this(dimensions, DEFAULT_CONTENT, DEFAULT_NOTE_PROPERTIES, ThemeControls.DEFAULT_COLOR);
    }

    /**
     * Creates a new Note object using all defaults
     */
    public Note() {
        this(DEFAULT_DIMENSIONS, DEFAULT_CONTENT, DEFAULT_NOTE_PROPERTIES,ThemeControls.DEFAULT_COLOR);
    }

    /**
     * Creates the stage and sets initial parameters
     */
    public void CreateStage() {
        _stage = new Stage();

        _appPane = new StackPane();
        _contentPane = new BorderPane();
        _controlPane = new BorderPane();
        _buttonRightTile = new TilePane();

        _scene = new Scene(_appPane, _dimensions.Width, _dimensions.Height, Color.TRANSPARENT);
        _stage.initStyle(StageStyle.TRANSPARENT);

        _scene.getStylesheets().addAll("main/note/theme/main.css", "main/note/theme/blue.css");

        _stage.setScene(_scene);
        _stage.setTitle("Sticky Note (" + (_noteID + 1) + ")");

        _stage.show();
        _stage.setAlwaysOnTop(true);

        _stage.setOnCloseRequest(windowEvent -> Close());

        ApplyNoteDimensions();
        CreateContent();
    }

    /**
     * Creates the content for the application
     */
    public void CreateContent() {
        _appPane.getChildren().add(_contentPane);
        _contentPane.setTop(_controlPane);

        _appPane.setId("app-pane");
        _contentPane.setId("content-pane");
        _controlPane.setId("control-pane");
        _buttonRightTile.setId("tile-right");

        _controls = new Controls(this, new Button("\u00D7"), new Button("\u002B"), new TextArea());

        // Add all button to the right button tile
        _buttonRightTile.getChildren().addAll(
            _controls.get_ThemeControls().ButtonDark,
            _controls.get_ThemeControls().ButtonBlue,
            _controls.get_ThemeControls().ButtonYellow,
            _controls.close
        );

        _controlPane.setLeft(_controls.create);
        _controlPane.setRight(_buttonRightTile);
        _contentPane.setCenter(_controls.text);
    }

    /**
     * Translates this note to be next to a given rect position. Will adjust as needed if there is not enough room.
     * @param position Position to move next to
     */
    public void TranslateNextTo(Rect position) {
        Rectangle2D r = Screen.getPrimary().getVisualBounds();
        Rect nRect = new Rect(position);

        nRect.PositionX += position.Width + NEW_NOTE_PADDING;

        if (nRect.PositionX + position.Width + NEW_NOTE_PADDING > r.getMaxX()) {
            if (nRect.PositionX + (position.Width * 0.15) > r.getMaxX()) {
                nRect.PositionX = position.PositionX - position.Width - NEW_NOTE_PADDING;
            } else {
                nRect.PositionX = r.getMaxX() - position.Width - NEW_NOTE_PADDING;
            }
        }

        for (Note note : notes) {
            if (note.get_Dimensions().PositionY == nRect.PositionY &&
                note.get_Dimensions().PositionX == nRect.PositionX) {
                nRect.PositionX += NEW_NOTE_PADDING * 4;
                nRect.PositionY += NEW_NOTE_PADDING * 4;
            }
        }

        _dimensions = nRect;
        ApplyNoteDimensions();
    }

    /**
     * Applies the current dimensions set on the Note to reflect the displayed Note
     */
    public void ApplyNoteDimensions() {
        _stage.setX(_dimensions.PositionX);
        _stage.setY(_dimensions.PositionY);
        _stage.setWidth(_dimensions.Width);
        _stage.setHeight(_dimensions.Height);
    }

    /**
     * Closes this note window
     */
    public void Close() {
        notes.remove(this);
        _stage.close();
    }

    /**
     * Sets a unique note name for this note
     */
    private void SetUniqueNoteID() {
        int uid = 0;

        for (Note note : notes) {
            if (note.get_noteID() == uid) uid++;
            else break;
        }

        _noteID = uid;
    }

    /**
     * Sets this note's color
     * @param color Color to set
     */
    public void set_Color(ThemeColor color) {
        _color = color;
    }

    /**
     * Gets this note's color
     * @return Color
     */
    public ThemeColor get_Color() {
        return _color;
    }

    /**
     * Gets the button right tile pane
     * @return TilePane
     */
    public TilePane get_ButtonRightTile() {
        return _buttonRightTile;
    }

    /**
     * Gets the Controls object for this note
     * @return Controls
     */
    public Controls get_Controls() {
        return _controls;
    }

    /**
     * Gets the app pane of this Note
     * @return StackPane for the app section
     */
    public StackPane get_AppPane() {
        return _appPane;
    }

    /**
     * Gets the dimensions of this Note
     * @return Dimensions
     */
    public Rect get_Dimensions() {
        return _dimensions;
    }

    /**
     * Gets the Note ID of this note
     * @return Note ID
     */
    public int get_noteID() {
        return _noteID;
    }

    /**
     * Gets the Stage for this note
     * @return Stage of this note
     */
    public Stage get_Stage() {
        return _stage;
    }

    /**
     * Gets the control Pane for this note
     * @return Pane for the control section
     */
    public Pane get_ControlPane() {
        return _controlPane;
    }

    /**
     * Gets the content Pane for this note
     * @return Pane for the content section
     */
    public Pane get_ContentPane() {
        return _contentPane;
    }
}
