package me.myogoo.extendedterminal.part;

import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.parts.AEBasePart;

public class EmptyPart extends AEBasePart {
    public EmptyPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 13, 12, 12, 14);
    }
}
