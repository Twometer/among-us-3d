package de.twometer.amogus.render

import de.twometer.neko.scene.Material
import de.twometer.neko.scene.nodes.Node
import de.twometer.neko.scene.nodes.RenderableNode
import java.util.function.Consumer

fun Node.updateMaterial(name: String, consumer: Consumer<Material>) {
    scanTree {
        if (it is RenderableNode && it.material.name == name)
            consumer.accept(it.material)
    }
}

fun Node.updateAllMaterials(consumer: Consumer<Material>) {
    scanTree {
        if (it is RenderableNode)
            consumer.accept(it.material)
    }
}