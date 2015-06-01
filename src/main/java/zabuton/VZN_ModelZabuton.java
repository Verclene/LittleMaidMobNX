package zabuton;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class VZN_ModelZabuton extends ModelBase {

	public ModelRenderer zabuton;

	
	public VZN_ModelZabuton() {
		zabuton = new ModelRenderer(this, 0, 0);
		zabuton.addBox(-6, -3, -6, 12, 3, 12);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        zabuton.render(f5);
    }
}
