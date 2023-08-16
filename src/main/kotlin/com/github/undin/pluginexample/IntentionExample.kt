package com.github.undin.pluginexample

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.util.ImportInsertHelperImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class IntentionExample : BaseElementAtCaretIntentionAction() {
    override fun getFamilyName(): String {
        // TODO: proper name
        return "Add import for Vanya"
    }

    override fun getText(): String = familyName

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return findApplicableContext(element) != null
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val context = findApplicableContext(element) ?: return
        val file = context.referenceExpr.containingKtFile
        val fqName = FqName("com.bandlab.uikit.icons.R")
        // Since 232 `ImportInsertHelperImpl.addImport` is deprecated
        ImportInsertHelperImpl.addImport(project, file, fqName, alias = context.name)
    }

    private fun findApplicableContext(element: PsiElement): Context? {
        // TODO: probably more precise check is required here
        val referenceExpr = element.parent as? KtNameReferenceExpression ?: return null
        if (referenceExpr.getIdentifier() != element) return null

        val name = referenceExpr.getReferencedNameAsName()
        if (name.identifierOrNullIfSpecial != "UIR") return null

        // Try to avoid cases when name is already imported
        if (referenceExpr.reference?.resolve() != null) return null

        return Context(referenceExpr, name)
    }

    data class Context(
        val referenceExpr: KtNameReferenceExpression,
        val name: Name
    )
}
