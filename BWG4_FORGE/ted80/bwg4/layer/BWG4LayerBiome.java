package ted80.bwg4.layer;

import java.util.ArrayList;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.IntCache;

public class BWG4LayerBiome extends BWG4Layer
{
    public static BiomeGenBase[] Biomes;
	public static ArrayList<BiomeGenBase> allowedBiomes = new ArrayList<BiomeGenBase>();
	public int worldID;
	
	//GOLD
	private BiomeGenBase[] IcePlains;
	private BiomeGenBase[] SnowPines;
	private BiomeGenBase[] PineForest;
	private BiomeGenBase[] Forest;
	private BiomeGenBase[] Jungle;
	private BiomeGenBase[] Tropical;
	private BiomeGenBase[] Rainforest; 
	private BiomeGenBase[] Desert;
	private BiomeGenBase[] Savanna;
	private BiomeGenBase[] Canyon;

    public BWG4LayerBiome(long par1, BWG4Layer par3GenLayer, WorldType par4WorldType, String[] Settings, int world, boolean remote)
    {
        super(par1);
		worldID = world;
		parent = par3GenLayer;
		
		//DEFAULT
		Biomes = (new BiomeGenBase[] { });
		boolean ocean = false;
		if(world == 0)
		{
			if(remote)
			{
				int count = 0;
				allowedBiomes.clear();
				for(int i = 0; i < Settings.length; i++)
				{
					if(Settings[i].equals("Shrubland=true")) { allowedBiomes.add(BiomeGenBase.BDshrubland); count++;  }
					else if(Settings[i].equals("Savanna=true")) { allowedBiomes.add(BiomeGenBase.BDsavanna); count++; }
					else if(Settings[i].equals("Desert=true")) { allowedBiomes.add(BiomeGenBase.BDdesert); count++; }
					else if(Settings[i].equals("Swampland=true")) { allowedBiomes.add(BiomeGenBase.BDswampland); count++; }
					else if(Settings[i].equals("Jungle=true")) { allowedBiomes.add(BiomeGenBase.BDjungle); count++; }
					else if(Settings[i].equals("RainForest=true")) { allowedBiomes.add(BiomeGenBase.BDrainforest); count++; }
					else if(Settings[i].equals("Grassland=true")) { allowedBiomes.add(BiomeGenBase.BDgrassland); count++; }
					else if(Settings[i].equals("Taiga=true")) { allowedBiomes.add(BiomeGenBase.BDtaiga); count++; }
					else if(Settings[i].equals("Pines=true")) { allowedBiomes.add(BiomeGenBase.BDpines); count++; }
					else if(Settings[i].equals("Forest Lakes=true")) { allowedBiomes.add(BiomeGenBase.BDforestlakes); count++; }
					else if(Settings[i].equals("Forest Hills=true")) { allowedBiomes.add(BiomeGenBase.BDforesthills); count++; }
					else if(Settings[i].equals("Forest=true")) { allowedBiomes.add(BiomeGenBase.BDforest); count++; }
					else if(Settings[i].equals("Plains=true")) { allowedBiomes.add(BiomeGenBase.BDplains); count++; }
					else if(Settings[i].equals("Snow Hills=true")) { allowedBiomes.add(BiomeGenBase.BDsnowhills); count++; }
					else if(Settings[i].equals("Snow Plains=true")) { allowedBiomes.add(BiomeGenBase.BDsnowplains); count++; }
					else if(Settings[i].equals("Snow Taiga=true")) { allowedBiomes.add(BiomeGenBase.BDsnowtaiga); count++; }
					else if(Settings[i].equals("Snow Forest=true")) { allowedBiomes.add(BiomeGenBase.BDsnowforest); count++; }
					else if(Settings[i].equals("Snow Pines=true")) { allowedBiomes.add(BiomeGenBase.BDsnowpines); count++; }
					else if(Settings[i].equals("Ocean=true")) { ocean = true; }
				}
				if(count == 0) 
				{ 
					if(ocean == true)
					{
						allowedBiomes.add(BiomeGenBase.BDocean); 
					}
					else
					{
						allowedBiomes.add(BiomeGenBase.BDplains); 
					}	
				}
				Biomes = allowedBiomes.toArray(new BiomeGenBase[0]);
			}
			else 
			{
				allowedBiomes.clear();
				allowedBiomes.add(BiomeGenBase.plains);
				Biomes = allowedBiomes.toArray(new BiomeGenBase[0]);
			}
		}	
		
		//GOLD
		IcePlains = new BiomeGenBase[] {BiomeGenBase.GOLD1iceOcean, BiomeGenBase.GOLD1icePlains, BiomeGenBase.GOLD1icePlains};
		SnowPines = new BiomeGenBase[] {BiomeGenBase.GOLD1snowpineLake, BiomeGenBase.GOLD1snowpineForest, BiomeGenBase.GOLD1snowpineHigh};
		PineForest = new BiomeGenBase[] {BiomeGenBase.GOLD2pineNormal, BiomeGenBase.GOLD2pineHills, BiomeGenBase.GOLD2pineMix, BiomeGenBase.GOLD2pineValleys};
		Forest = new BiomeGenBase[] {BiomeGenBase.GOLD2forestNormal, BiomeGenBase.GOLD2forestHigh, BiomeGenBase.GOLD2forestLakes, BiomeGenBase.GOLD2forestPlains};
		Jungle = new BiomeGenBase[] {BiomeGenBase.GOLD3jungleFlat, BiomeGenBase.GOLD3jungleHilly, BiomeGenBase.GOLD3jungleDeep};
		Tropical = new BiomeGenBase[] {BiomeGenBase.GOLD3tropicSea, BiomeGenBase.GOLD3tropicSea, BiomeGenBase.GOLD3tropicIsland, BiomeGenBase.GOLD3tropicSea};
		Rainforest = new BiomeGenBase[] {BiomeGenBase.GOLD3rainFlat, BiomeGenBase.GOLD3rainHills, BiomeGenBase.GOLD3rainLakes, BiomeGenBase.GOLD3rainLake, BiomeGenBase.GOLD3rainSwamp};
		Desert = new BiomeGenBase[] {BiomeGenBase.GOLD4desertFlat, BiomeGenBase.GOLD4desertHills, BiomeGenBase.GOLD4desertFlat, BiomeGenBase.GOLD4desertLakes};
		Savanna = new BiomeGenBase[] {BiomeGenBase.GOLD4savannaFields, BiomeGenBase.GOLD4savannaForest, BiomeGenBase.GOLD4savannaHills};
		Canyon = new BiomeGenBase[] {BiomeGenBase.GOLD4canyonHIGH1, BiomeGenBase.GOLD4canyonHIGH2, BiomeGenBase.GOLD4canyonHIGH3, BiomeGenBase.GOLD4canyonFields};
	} 
	
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] var5 = this.parent.getInts(par1, par2, par3, par4);
        int[] var6 = IntCache.getIntCache(par3 * par4);

        for (int var7 = 0; var7 < par4; ++var7)
        {
            for (int var8 = 0; var8 < par3; ++var8)
            {
                this.initChunkSeed((long)(var8 + par1), (long)(var7 + par2));
                int var9 = var5[var8 + var7 * par3];

				if (worldID == 1) 
				{
					if (var9 == 0) { var6[var8 + var7 * par3] = SnowPines[nextInt(SnowPines.length)].biomeID; }
					else if (var9 == 1) { var6[var8 + var7 * par3] = IcePlains[nextInt(IcePlains.length)].biomeID; }
					else if (var9 == 2) { var6[var8 + var7 * par3] = SnowPines[nextInt(SnowPines.length)].biomeID; }
					else if (var9 == 10) { var6[var8 + var7 * par3] = PineForest[nextInt(PineForest.length)].biomeID; }
					else if (var9 == 11) { var6[var8 + var7 * par3] = Forest[nextInt(Forest.length)].biomeID; }
					else if (var9 == 20) { var6[var8 + var7 * par3] = Jungle[nextInt(Jungle.length)].biomeID; }
					else if (var9 == 21) { var6[var8 + var7 * par3] = Tropical[nextInt(Tropical.length)].biomeID; }
					else if (var9 == 22) { var6[var8 + var7 * par3] = Rainforest[nextInt(Rainforest.length)].biomeID; }
					else if (var9 == 30) { var6[var8 + var7 * par3] = Desert[nextInt(Desert.length)].biomeID; }
					else if (var9 == 31) { var6[var8 + var7 * par3] = Savanna[nextInt(Savanna.length)].biomeID; }
					else if (var9 == 32) { var6[var8 + var7 * par3] = Canyon[nextInt(Canyon.length)].biomeID; }
					else { var6[var8 + var7 * par3] = Jungle[nextInt(Jungle.length)].biomeID; }
				}
				else
				{
					if (var9 == 0)
					{
						var6[var8 + var7 * par3] = BiomeGenBase.BDocean.biomeID;
					}
					else if (var9 == BiomeGenBase.mushroomIsland.biomeID)
					{
						var6[var8 + var7 * par3] = BiomeGenBase.mushroomIsland.biomeID;
					}
					else 
					{
						var6[var8 + var7 * par3] = this.Biomes[this.nextInt(this.Biomes.length)].biomeID;
					}
				}	
            }
        }

        return var6;
    }
}