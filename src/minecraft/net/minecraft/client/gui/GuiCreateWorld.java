package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

import ted80.api.DefaultBiomeList;


@SideOnly(Side.CLIENT)
public class GuiCreateWorld extends GuiScreen
{
    private GuiScreen parentGuiScreen;
    private GuiTextField textboxWorldName;
    private GuiTextField textboxSeed;
    private String folderName;

    /** hardcore', 'creative' or 'survival */
    private String gameMode = "survival";
    private boolean generateStructures = true;
    private boolean commandsAllowed = false;

    /** True iif player has clicked buttonAllowCommands at least once */
    private boolean commandsToggled = false;

    /** toggles when GUIButton 7 is pressed */
    private boolean bonusItems = false;

    /** True if and only if gameMode.equals("hardcore") */
    private boolean isHardcore = false;
    private boolean createClicked;

    /**
     * True if the extra options (Seed box, structure toggle button, world type button, etc.) are being shown
     */
    private boolean moreOptions;

    /** The GUIButton that you click to change game modes. */
    private GuiButton buttonGameMode;

    /**
     * The GUIButton that you click to get to options like the seed when creating a world.
     */
    private GuiButton moreWorldOptions;

    /** The GuiButton in the 'More World Options' screen. Toggles ON/OFF */
    private GuiButton buttonGenerateStructures;
    private GuiButton buttonBonusItems;

    /** The GuiButton in the more world options screen. */
    private GuiButton buttonWorldType;
    private GuiButton buttonAllowCommands;

    /** GuiButton in the more world options screen. */
    private GuiButton buttonCustomize;

    /** The first line of text describing the currently selected game mode. */
    private String gameModeDescriptionLine1;

    /** The second line of text describing the currently selected game mode. */
    private String gameModeDescriptionLine2;

    /** The current textboxSeed text */
    private String seed;

    /** E.g. New World, Neue Welt, Nieuwe wereld, Neuvo Mundo */
    private String localizedNewWorldText;
    private int worldTypeId = 0;

    /** Generator options to use when creating the world. */
    public String generatorOptionsToUse = "";

    /**
     * If the world name is one of these, it'll be surrounded with underscores.
     */
	 
	//BWG4
	private GuiButton buttonDefault;
	private GuiButton buttonLargeBiomes;
	private GuiButton buttonBeta;
	private GuiButton buttonInfdev;
	private GuiButton buttonIndev1;
	private GuiButton buttonIndev2;
	private GuiButton buttonIsland;
	private GuiButton buttonSkyland;
	private GuiButton buttonSkydimension;
	private GuiButton buttonCustomDefault1;
	private GuiButton buttonCustomDefault2;
	private GuiButton buttonSkyBlock;
	
    private int selectDefault = 1;
    private int selectLargeBiomes = 1;
    private int selectBeta = 0;
    private int selectInfdev = 0;
    private int selectIndev1 = 0;
    private int selectIndev2 = 0;
    private int selectIsland = 0;
    private int selectSkyland = 0;
    private int selectSkydimension = 0;
    private int selectSkyBlock = 0;
	
	private int currentWorldtype = 1;
	private int maxWorldtype = 0;
	 
    private static final String[] ILLEGAL_WORLD_NAMES = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public GuiCreateWorld(GuiScreen par1GuiScreen)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.seed = "";
        this.localizedNewWorldText = StatCollector.translateToLocal("selectWorld.newWorld");
		
		//count worldtypes
		maxWorldtype = 0;
		for(int i = 0; i < WorldType.worldTypes.length; i++)
		{
			if(WorldType.worldTypes[i] != null && WorldType.worldTypes[i].getCanBeCreated())
			{
				maxWorldtype++;
			}
		}
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.textboxWorldName.updateCursorCounter();
        this.textboxSeed.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, var1.translateKey("selectWorld.create")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, var1.translateKey("gui.cancel")));
        this.buttonList.add(this.buttonGameMode = new GuiButton(2, this.width / 2 - 75, 115, 150, 20, var1.translateKey("selectWorld.gameMode")));
        this.buttonList.add(this.moreWorldOptions = new GuiButton(3, this.width / 2 - 75, 187, 150, 20, var1.translateKey("selectWorld.moreWorldOptions")));
        this.buttonList.add(this.buttonGenerateStructures = new GuiButton(4, this.width / 2 - 165, 100, 160, 20, var1.translateKey("selectWorld.mapFeatures")));
        this.buttonGenerateStructures.drawButton = false;
        this.buttonList.add(this.buttonBonusItems = new GuiButton(7, this.width / 2 - 165, 125, 160, 20, var1.translateKey("selectWorld.bonusItems")));
        this.buttonBonusItems.drawButton = false;
        this.buttonList.add(this.buttonWorldType = new GuiButton(5, this.width / 2 + 5, 100, 160, 20, var1.translateKey("selectWorld.mapType")));
        this.buttonWorldType.drawButton = false;
        this.buttonList.add(this.buttonAllowCommands = new GuiButton(6, this.width / 2 - 165, 151, 160, 20, var1.translateKey("selectWorld.allowCommands")));
        this.buttonAllowCommands.drawButton = false;
        this.buttonList.add(this.buttonCustomize = new GuiButton(8, this.width / 2 + 5, 125, 160, 20, var1.translateKey("selectWorld.customizeType")));
        this.buttonCustomize.drawButton = false;
		
		//BWG4 - CREATE BUTTONS
        this.buttonList.add(this.buttonDefault = new GuiButton(9, this.width / 2 + 5, 125, 160, 20, "buttonDefault")); 
        this.buttonList.add(this.buttonLargeBiomes = new GuiButton(10, this.width / 2 + 5, 125, 160, 20, "buttonLargeBiomes")); 
        this.buttonList.add(this.buttonBeta = new GuiButton(11, this.width / 2 + 5, 125, 160, 20, "buttonBeta")); 
        this.buttonList.add(this.buttonInfdev = new GuiButton(12, this.width / 2 + 5, 125, 160, 20, "buttonInfdev")); 
        this.buttonList.add(this.buttonIndev1 = new GuiButton(13, this.width / 2 + 5, 125, 160, 20, "buttonIndev1")); 
        this.buttonList.add(this.buttonIndev2 = new GuiButton(17, this.width / 2 + 5, 150, 160, 20, "buttonIndev2")); 
        this.buttonList.add(this.buttonIsland = new GuiButton(14, this.width / 2 + 5, 125, 160, 20, "buttonIsland")); 
        this.buttonList.add(this.buttonSkyland = new GuiButton(15, this.width / 2 + 5, 125, 160, 20, "buttonSkyland")); 
        this.buttonList.add(this.buttonSkydimension = new GuiButton(16, this.width / 2 + 5, 125, 160, 20, "buttonSkydimension")); 
        this.buttonList.add(this.buttonCustomDefault1 = new GuiButton(19, this.width / 2 + 5, 150, 160, 20, var1.translateKey("selectWorld.customizeType"))); 
        this.buttonList.add(this.buttonCustomDefault2 = new GuiButton(20, this.width / 2 + 5, 150, 160, 20, var1.translateKey("selectWorld.customizeType"))); 
        this.buttonList.add(this.buttonSkyBlock = new GuiButton(21, this.width / 2 + 5, 125, 160, 20, "buttonSkyBlock")); 
		
		//BWG4 - DRAW BUTTONS
        this.buttonDefault.drawButton = false;
        this.buttonLargeBiomes.drawButton = false;
        this.buttonBeta.drawButton = false;
        this.buttonInfdev.drawButton = false;
        this.buttonIndev1.drawButton = false;
        this.buttonIndev2.drawButton = false;
        this.buttonIsland.drawButton = false;
        this.buttonSkyland.drawButton = false;
        this.buttonSkydimension.drawButton = false;
		this.buttonCustomDefault1.drawButton = false;
		this.buttonCustomDefault2.drawButton = false;	
		this.buttonSkyBlock.drawButton = false;
		this.buttonSkyBlock.enabled = true;
		
        this.textboxWorldName = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.textboxWorldName.setFocused(true);
        this.textboxWorldName.setText(this.localizedNewWorldText);
        this.textboxSeed = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.textboxSeed.setText(this.seed);
        this.func_82288_a(this.moreOptions);
        this.makeUseableName();
        this.updateButtonText();
		
		if(generatorOptionsToUse.length() < 2)
		{
			generatorOptionsToUse = DefaultBiomeList.getDefaultString();
		}
    }

    /**
     * Makes a the name for a world save folder based on your world name, replacing specific characters for _s and
     * appending -s to the end until a free name is available.
     */
    private void makeUseableName()
    {
        this.folderName = this.textboxWorldName.getText().trim();
        char[] var1 = ChatAllowedCharacters.allowedCharactersArray;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            char var4 = var1[var3];
            this.folderName = this.folderName.replace(var4, '_');
        }

        if (MathHelper.stringNullOrLengthZero(this.folderName))
        {
            this.folderName = "World";
        }

        this.folderName = func_73913_a(this.mc.getSaveLoader(), this.folderName);
    }

    private void updateButtonText()
    {
        StringTranslate var1 = StringTranslate.getInstance();
        this.buttonGameMode.displayString = var1.translateKey("selectWorld.gameMode") + " " + var1.translateKey("selectWorld.gameMode." + this.gameMode);
        this.gameModeDescriptionLine1 = var1.translateKey("selectWorld.gameMode." + this.gameMode + ".line1");
        this.gameModeDescriptionLine2 = var1.translateKey("selectWorld.gameMode." + this.gameMode + ".line2");
        this.buttonGenerateStructures.displayString = var1.translateKey("selectWorld.mapFeatures") + " ";

        if (this.generateStructures)
        {
            this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + var1.translateKey("options.on");
        }
        else
        {
            this.buttonGenerateStructures.displayString = this.buttonGenerateStructures.displayString + var1.translateKey("options.off");
        }

        this.buttonBonusItems.displayString = var1.translateKey("selectWorld.bonusItems") + " ";

        if (this.bonusItems && !this.isHardcore)
        {
            this.buttonBonusItems.displayString = this.buttonBonusItems.displayString + var1.translateKey("options.on");
        }
        else
        {
            this.buttonBonusItems.displayString = this.buttonBonusItems.displayString + var1.translateKey("options.off");
        }

        this.buttonWorldType.displayString = var1.translateKey("selectWorld.mapType") + " " + var1.translateKey(WorldType.worldTypes[this.worldTypeId].getTranslateName());
        this.buttonAllowCommands.displayString = var1.translateKey("selectWorld.allowCommands") + " ";

        if (this.commandsAllowed && !this.isHardcore)
        {
            this.buttonAllowCommands.displayString = this.buttonAllowCommands.displayString + var1.translateKey("options.on");
        }
        else
        {
            this.buttonAllowCommands.displayString = this.buttonAllowCommands.displayString + var1.translateKey("options.off");
        }
		
		
		//====== BWG4 ======
		
		if(selectDefault == 0) { buttonDefault.displayString = "Better Default: " + var1.translateKey("options.off"); }
		else { buttonDefault.displayString = "Better Default: " + var1.translateKey("options.on"); }
		
		if(selectLargeBiomes == 0) { buttonLargeBiomes.displayString = "Better Default: " + var1.translateKey("options.off"); } 
		else { buttonLargeBiomes.displayString = "Better Default: " + var1.translateKey("options.on"); }
		
		if(selectBeta == 0) { buttonBeta.displayString = "Biomes: Beta"; } 
		else { buttonBeta.displayString = "Biomes: Default"; }
		
		if(selectInfdev == 0) { buttonInfdev.displayString = "Snow World: " + var1.translateKey("options.off"); }
		else { buttonInfdev.displayString = "Snow World: " + var1.translateKey("options.on"); }
		
		if(selectIndev1 == 0) { buttonIndev1.displayString = "Type: Inland"; }
		else { buttonIndev1.displayString = "Type: Floating"; }
		
		if(selectIndev2 == 0) { buttonIndev2.displayString = "Theme: Normal"; }
		else if(selectIndev2 == 1) { buttonIndev2.displayString = "Theme: Hell"; }
		else if(selectIndev2 == 2) { buttonIndev2.displayString = "Theme: Paradise"; } 
		else if(selectIndev2 == 3) { buttonIndev2.displayString = "Theme: Woods"; } 
		else { buttonIndev2.displayString = "Theme: Snow"; }
		
		if(selectIsland == 0) { buttonIsland.displayString = "Theme: Normal"; }
		else if(selectIsland == 1) { buttonIsland.displayString = "Theme: -"; }
		else if(selectIsland == 2) { buttonIsland.displayString = "Theme: -"; }
		else if(selectIsland == 3) { buttonIsland.displayString = "Theme: Paradise"; }
		else { buttonIsland.displayString = "Theme: -"; }
		
		if(selectSkyland == 0) { buttonSkyland.displayString = "Theme: Normal"; }
		else if(selectSkyland == 1) { buttonSkyland.displayString = "Theme: -"; }
		else if(selectSkyland == 2) { buttonSkyland.displayString = "Theme: Snow"; }
		else if(selectSkyland == 3) { buttonSkyland.displayString = "Theme: Jungle"; }
		else { buttonSkyland.displayString = "Theme: -"; }
		
		if(selectSkyBlock == 0) { buttonSkyBlock.displayString = "Type: Classic"; }
		else if(selectSkyBlock == 1) { buttonSkyBlock.displayString = "Type: Extended"; }
		else { buttonSkyBlock.displayString = "Type: Endless"; }
		
		if(selectSkydimension == 0) { buttonSkydimension.displayString = "Biomes: Default"; }
		else if(selectSkydimension == 1) { buttonSkydimension.displayString = "Biomes: Beta"; }
		else { buttonSkydimension.displayString = "Biomes: Adventure"; }
    }

    public static String func_73913_a(ISaveFormat par0ISaveFormat, String par1Str)
    {
        par1Str = par1Str.replaceAll("[\\./\"]", "_");
        String[] var2 = ILLEGAL_WORLD_NAMES;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            String var5 = var2[var4];

            if (par1Str.equalsIgnoreCase(var5))
            {
                par1Str = "_" + par1Str + "_";
            }
        }

        while (par0ISaveFormat.getWorldInfo(par1Str) != null)
        {
            par1Str = par1Str + "-";
        }

        return par1Str;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 1)
            {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            else if (par1GuiButton.id == 0)
            {
                this.mc.displayGuiScreen((GuiScreen)null);

                if (this.createClicked)
                {
                    return;
                }

                this.createClicked = true;
                long var2 = (new Random()).nextLong();
                String var4 = this.textboxSeed.getText();

                if (!MathHelper.stringNullOrLengthZero(var4))
                {
                    try
                    {
                        long var5 = Long.parseLong(var4);

                        if (var5 != 0L)
                        {
                            var2 = var5;
                        }
                    }
                    catch (NumberFormatException var7)
                    {
                        var2 = (long)var4.hashCode();
                    }
                }
				
				//====== BWG4 ======
				if(selectDefault == 1 && worldTypeId == 0) { worldTypeId = 10; }
				if(selectLargeBiomes == 1 && worldTypeId == 2) { worldTypeId = 11; }
				if(selectBeta == 1 && worldTypeId == 12) { worldTypeId = 13; }
				if(selectIndev1 == 1 && worldTypeId == 17) { worldTypeId = 18; }
				if(selectSkydimension == 1 && worldTypeId == 25) { worldTypeId = 26; }
				//==================

                EnumGameType var8 = EnumGameType.getByName(this.gameMode);
                WorldSettings var6 = new WorldSettings(var2, var8, this.generateStructures, this.isHardcore, WorldType.worldTypes[this.worldTypeId]);
				
				//====== BWG4 ======
				if(selectIndev2 == 1 && worldTypeId == 16) { var6.func_82750_a("2"); }
				else if(selectIndev2 == 2 && worldTypeId == 16) { var6.func_82750_a("3"); }
				else if(selectIndev2 == 3 && worldTypeId == 16) { var6.func_82750_a("4"); }
				else if(selectIndev2 == 4 && worldTypeId == 16) { var6.func_82750_a("5"); }
				else if(selectIndev2 == 1 && worldTypeId == 17) { var6.func_82750_a("2"); }
				else if(selectIndev2 == 2 && worldTypeId == 17) { var6.func_82750_a("3"); }
				else if(selectIndev2 == 3 && worldTypeId == 17) { var6.func_82750_a("4"); }
				else if(selectIndev2 == 4 && worldTypeId == 17) { var6.func_82750_a("5"); }
				else if(selectInfdev == 0 && worldTypeId == 15) { var6.func_82750_a("1"); }
				else if(selectInfdev == 1 && worldTypeId == 15) { var6.func_82750_a("2"); } 
				else if(selectIsland == 0 && worldTypeId == 21) { var6.func_82750_a("1"); }
				else if(selectIsland == 1 && worldTypeId == 21) { var6.func_82750_a("2"); }
				else if(selectIsland == 2 && worldTypeId == 21) { var6.func_82750_a("3"); }
				else if(selectIsland == 3 && worldTypeId == 21) { var6.func_82750_a("4"); }
				else if(selectIsland == 4 && worldTypeId == 21) { var6.func_82750_a("5"); }
				else if(selectSkyland == 0 && worldTypeId == 22) { var6.func_82750_a("1"); }
				else if(selectSkyland == 1 && worldTypeId == 22) { var6.func_82750_a("2"); }
				else if(selectSkyland == 2 && worldTypeId == 22) { var6.func_82750_a("3"); }
				else if(selectSkyland == 3 && worldTypeId == 22) { var6.func_82750_a("4"); }
				else if(selectSkyland == 4 && worldTypeId == 22) { var6.func_82750_a("5"); }
				else if(selectSkyBlock == 0 && worldTypeId == 23) { var6.func_82750_a("1"); }
				else if(selectSkyBlock == 1 && worldTypeId == 23) { var6.func_82750_a("2"); }
				else if(selectSkyBlock == 2 && worldTypeId == 23) { var6.func_82750_a("3"); }

				if(worldTypeId == 1 || worldTypeId == 10 || worldTypeId == 11) 
				{ 
					var6.func_82750_a(this.generatorOptionsToUse);
				} 

				//==================

                if (this.bonusItems && !this.isHardcore)
                {
                    var6.enableBonusChest();
                }

                if (this.commandsAllowed && !this.isHardcore)
                {
                    var6.enableCommands();
                }

                this.mc.launchIntegratedServer(this.folderName, this.textboxWorldName.getText().trim(), var6);
            }
            else if (par1GuiButton.id == 3)
            {
                this.func_82287_i();
            }
            else if (par1GuiButton.id == 2)
            {
                if (this.gameMode.equals("survival"))
                {
                    if (!this.commandsToggled)
                    {
                        this.commandsAllowed = false;
                    }

                    this.isHardcore = false;
                    this.gameMode = "hardcore";
                    this.isHardcore = true;
                    this.buttonAllowCommands.enabled = false;
                    this.buttonBonusItems.enabled = false;
                    this.updateButtonText();
                }
                else if (this.gameMode.equals("hardcore"))
                {
                    if (!this.commandsToggled)
                    {
                        this.commandsAllowed = true;
                    }

                    this.isHardcore = false;
                    this.gameMode = "creative";
                    this.updateButtonText();
                    this.isHardcore = false;
                    this.buttonAllowCommands.enabled = true;
                    this.buttonBonusItems.enabled = true;
                }
                else
                {
                    if (!this.commandsToggled)
                    {
                        this.commandsAllowed = false;
                    }

                    this.gameMode = "survival";
                    this.updateButtonText();
                    this.buttonAllowCommands.enabled = true;
                    this.buttonBonusItems.enabled = true;
                    this.isHardcore = false;
                }

                this.updateButtonText();
            }
            else if (par1GuiButton.id == 4)
            {
                this.generateStructures = !this.generateStructures;
                this.updateButtonText();
            }
            else if (par1GuiButton.id == 7)
            {
                this.bonusItems = !this.bonusItems;
                this.updateButtonText();
            }
            else if (par1GuiButton.id == 5)
            {
				currentWorldtype++;
				if(currentWorldtype > maxWorldtype)
				{
					currentWorldtype = 1;
				}		
				
                ++this.worldTypeId;

                if (this.worldTypeId >= WorldType.worldTypes.length)
                {
                    this.worldTypeId = 0;
                }

                while (WorldType.worldTypes[this.worldTypeId] == null || !WorldType.worldTypes[this.worldTypeId].getCanBeCreated())
                {
                    ++this.worldTypeId;

                    if (this.worldTypeId >= WorldType.worldTypes.length)
                    {
                        this.worldTypeId = 0;
                    }
                }

				if(worldTypeId == 0 || worldTypeId == 2)
				{
					generatorOptionsToUse = DefaultBiomeList.getDefaultString();
				}
				else
				{
					generatorOptionsToUse = "";
				}
				
                this.updateButtonText();
                this.func_82288_a(this.moreOptions);
            }
            else if (par1GuiButton.id == 6)
            {
                this.commandsToggled = true;
                this.commandsAllowed = !this.commandsAllowed;
                this.updateButtonText();
            }
            else if (par1GuiButton.id == 8)
            {
                //this.mc.displayGuiScreen(new GuiCreateFlatWorld(this, this.generatorOptionsToUse));
				WorldType.worldTypes[this.worldTypeId].onCustomizeButton(this.mc, this);
            }
            else if (par1GuiButton.id == 9) 
            {
                if(selectDefault == 0) { selectDefault = 1; } else { selectDefault = 0; }
				this.func_82288_a(this.moreOptions);
            	updateButtonText();
            }
            else if (par1GuiButton.id == 10) 
            {
                if(selectLargeBiomes == 0) { selectLargeBiomes = 1; } else { selectLargeBiomes = 0; }
				this.func_82288_a(this.moreOptions);
            	updateButtonText();
            }
            else if (par1GuiButton.id == 11) 
            {
                if(selectBeta == 0) { selectBeta = 1; } else { selectBeta = 0; }
            	updateButtonText();
            }
            else if (par1GuiButton.id == 12) 
            {
                if(selectInfdev == 0) { selectInfdev = 1; } else { selectInfdev = 0; }
            	updateButtonText();
            }
            else if (par1GuiButton.id == 13) 
            {
                if(selectIndev1 == 0) { selectIndev1 = 1; } else { selectIndev1 = 0; }
				this.func_82288_a(this.moreOptions);
            	updateButtonText();
            }
            else if (par1GuiButton.id == 14) 
            {
                /*if(selectIsland == 0) { selectIsland = 1; } else if(selectIsland == 1) { selectIsland = 2; } else*/ if(selectIsland == 0) { selectIsland = 3; } /* else if(selectIsland == 3) { selectIsland = 4; }*/ else { selectIsland = 0; }
            	updateButtonText();
            }
            else if (par1GuiButton.id == 15) 
            {
                /*if(selectSkyland == 0) { selectSkyland = 1; } else*/ if(selectSkyland == 0) { selectSkyland = 2; } else if(selectSkyland == 2) { selectSkyland = 3; } /* else if(selectSkyland == 3) { selectSkyland = 4; }*/ else { selectSkyland = 0; }
            	updateButtonText();
            }
            else if (par1GuiButton.id == 16) 
            {
                if(selectSkydimension == 0) { selectSkydimension = 1; } /*else if(selectSkydimension == 1) { selectSkydimension = 2; }*/ else { selectSkydimension = 0; }
            	updateButtonText();
            }
            else if (par1GuiButton.id == 17) 
            {
                if(selectIndev2 == 0) { selectIndev2 = 1; } else if(selectIndev2 == 1) { selectIndev2 = 2; } else if(selectIndev2 == 2) { selectIndev2 = 3; } else if(selectIndev2 == 3) { selectIndev2 = 4; } else { selectIndev2 = 0; }
            	updateButtonText();
            }
			else if (par1GuiButton.id == 19)
			{
				this.mc.displayGuiScreen(new BWG4GuiDefault(this, this.generatorOptionsToUse));
			}
			else if (par1GuiButton.id == 20)
			{
				this.mc.displayGuiScreen(new BWG4GuiDefault(this, this.generatorOptionsToUse));
			}
			else if (par1GuiButton.id == 21)
			{
                if(selectSkyBlock == 0) { selectSkyBlock = 1; } else if(selectSkyBlock == 1) { selectSkyBlock = 0; } else { selectSkyBlock = 0; }
				updateButtonText();
			}
        }
    }

    private void func_82287_i()
    {
        this.func_82288_a(!this.moreOptions);
    }

    private void func_82288_a(boolean par1)
    {
        this.moreOptions = par1;
        this.buttonGameMode.drawButton = !this.moreOptions;
        this.buttonGenerateStructures.drawButton = this.moreOptions;
        this.buttonBonusItems.drawButton = this.moreOptions;
        this.buttonWorldType.drawButton = this.moreOptions;
        this.buttonAllowCommands.drawButton = this.moreOptions;
        //this.buttonCustomize.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.FLAT;
		this.buttonCustomize.drawButton = this.moreOptions && (WorldType.worldTypes[this.worldTypeId].isCustomizable());
		
		//BWG4
		this.buttonDefault.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.DEFAULT;
		this.buttonCustomDefault1.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.DEFAULT && selectDefault == 1;
		this.buttonLargeBiomes.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.LARGE_BIOMES;
		this.buttonCustomDefault2.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.LARGE_BIOMES && selectLargeBiomes == 1;
		this.buttonBeta.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4BETA1;
		this.buttonInfdev.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4INFDEV;
		this.buttonIndev1.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4INDEV1;
		this.buttonIndev2.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4INDEV1;
		this.buttonIsland.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4ISLAND;
		this.buttonSkyland.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4SKYLAND;
		this.buttonSkydimension.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4SKY1;		
		this.buttonSkyBlock.drawButton = this.moreOptions && WorldType.worldTypes[this.worldTypeId] == WorldType.BWG4SKYBLOCK;	
		
        StringTranslate var2;

        if (this.moreOptions)
        {
            var2 = StringTranslate.getInstance();
            this.moreWorldOptions.displayString = var2.translateKey("gui.done");
        }
        else
        {
            var2 = StringTranslate.getInstance();
            this.moreWorldOptions.displayString = var2.translateKey("selectWorld.moreWorldOptions");
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (this.textboxWorldName.isFocused() && !this.moreOptions)
        {
            this.textboxWorldName.textboxKeyTyped(par1, par2);
            this.localizedNewWorldText = this.textboxWorldName.getText();
        }
        else if (this.textboxSeed.isFocused() && this.moreOptions)
        {
            this.textboxSeed.textboxKeyTyped(par1, par2);
            this.seed = this.textboxSeed.getText();
        }

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }

        ((GuiButton)this.buttonList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
        this.makeUseableName();
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.moreOptions)
        {
            this.textboxSeed.mouseClicked(par1, par2, par3);
        }
        else
        {
            this.textboxWorldName.mouseClicked(par1, par2, par3);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        StringTranslate var4 = StringTranslate.getInstance();
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("selectWorld.create"), this.width / 2, 20, 16777215);

        if (this.moreOptions)
        {
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
            //this.drawString(this.fontRenderer, var4.translateKey("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, 10526880);
            //this.drawString(this.fontRenderer, var4.translateKey("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, 10526880);
            this.textboxSeed.drawTextBox();
            this.drawString(this.fontRenderer, "(" + currentWorldtype + "/" + maxWorldtype + ")", this.width / 2 + 120, 90, 10526880);  
        }
        else
        {
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, var4.translateKey("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85, 10526880);
            this.textboxWorldName.drawTextBox();
            this.drawString(this.fontRenderer, this.gameModeDescriptionLine1, this.width / 2 - 100, 137, 10526880);
            this.drawString(this.fontRenderer, this.gameModeDescriptionLine2, this.width / 2 - 100, 149, 10526880);
        }

        super.drawScreen(par1, par2, par3);
    }

    public void func_82286_a(WorldInfo par1WorldInfo)
    {
        this.localizedNewWorldText = StatCollector.translateToLocalFormatted("selectWorld.newWorld.copyOf", new Object[] {par1WorldInfo.getWorldName()});
        this.seed = par1WorldInfo.getSeed() + "";
        this.worldTypeId = par1WorldInfo.getTerrainType().getWorldTypeID();
        this.generatorOptionsToUse = par1WorldInfo.getGeneratorOptions();
        this.generateStructures = par1WorldInfo.isMapFeaturesEnabled();
        this.commandsAllowed = par1WorldInfo.areCommandsAllowed();

		if(worldTypeId == 10) { selectDefault = 1; worldTypeId = 0; }
		else if(worldTypeId == 11) { selectLargeBiomes = 1; worldTypeId = 2; }
		else if(worldTypeId == 13) { selectBeta = 1; worldTypeId = 12; }
		else if(worldTypeId == 14) { selectBeta = 2; worldTypeId = 12; }
		else if(worldTypeId == 18) { selectIndev1 = 1; worldTypeId = 17; }
		else if(worldTypeId == 26) { selectSkydimension = 1; worldTypeId = 25; }
		else if(worldTypeId == 27) { selectSkydimension = 2; worldTypeId = 25; }	
		
		//count worldtypes
		int newnumber = 1;
		for(int i = 0; i < WorldType.worldTypes.length; i++)
		{	
			if(WorldType.worldTypes[i] != null && WorldType.worldTypes[i].getCanBeCreated())
			{
				if(i == worldTypeId)
				{
					currentWorldtype = newnumber;
				}
				else
				{
					newnumber++;
				}
			}
		}
		
        if (par1WorldInfo.isHardcoreModeEnabled())
        {
            this.gameMode = "hardcore";
        }
        else if (par1WorldInfo.getGameType().isSurvivalOrAdventure())
        {
            this.gameMode = "survival";
        }
        else if (par1WorldInfo.getGameType().isCreative())
        {
            this.gameMode = "creative";
        }
    }
}