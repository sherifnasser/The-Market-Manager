package com.sherifnasser.themarketmanager.ui.widget

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.navigation.NavController
import com.sherifnasser.themarketmanager.databinding.AppSearchViewBinding
import com.sherifnasser.themarketmanager.util.showKeyboard
import com.sherifnasser.themarketmanager.util.hideKeyboard

class AppSearchView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    /***
    I built this class because I didn't like the normal search view. I wanted it to have an animation also I wanted to customize it easily.
    I've wrote this class and I was thinking to make it a library after finishing this project.
    So if read this comment from the future you should know that I was thinking to introduce the concept of lifecycle of the search view
    and make the search working with the navigation architecture component.
     *** Thanks for reading ***
     ***/


    private val binding = AppSearchViewBinding.inflate(LayoutInflater.from(context), this, true)

    // To create circular reveal animation.
    private val cX by lazy{binding.clearBtn.let{btn->(btn.right+btn.left)/2}}
    private val cY by lazy { (height / 2) }
    private val revealRadius by lazy { width.toFloat() }
    private val revealDuration by lazy { 250L }

    // To make the circular reveal ends at back btn
    private val cXBackBtn by lazy {binding.backBtn.let{btn->(btn.right+btn.left)/2}}

    // Since there is no SAM-conversion with interface in kotlin, I decided to use higher-order function to avoid the ugly code.
    private lateinit var onQueryTextChangedListener: (CharSequence?) -> Unit

    private lateinit var appSearchListener:AppSearchListener

    // Initialize
    init {

        // When click back button
        binding.backBtn.setOnClickListener { closeSearchWithAnimation() }

        // When click the clear button (the query editText will hide it when query is empty).
        binding.clearBtn.setOnClickListener { clearQuery() }

        // The query editText
        binding.queryEditText.apply {

            // When the query changes
            doOnTextChanged { query, _, _, _ ->
                // Send query even it is empty.
                if (::onQueryTextChangedListener.isInitialized)
                    onQueryTextChangedListener(query)

                // Hide the clear btn when query is empty (it will called when user clicks clear btn).
                binding.clearBtn.let {
                    if (query!!.trim().isEmpty()) hideView(it)
                    else showView(it)
                }
            }

            // When the user hit the Enter key hde the keyboard.
            setOnEditorActionListener { _, actionId, _ ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    handled = true
                }
                handled
            }
        }
    }

    // For navigation architecture component.
    fun setupWithNavController(
      navController: NavController,
      topLevelDestinationIds: Set<Int>,
      destinationHasSearchId: Int
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            /*
            The user may go to a destination has search. They open it and do their search. Click the result and go to the result page and back again.
            But the user expects that search wasn't closed. So it will decide if show the search view or to close it.
             */

            /*
             If the search was opened before, check if current destination is a destination has search and just show it.
             Or if the new destination is a top level destination close search (without animation).
             And finally if it is neither a destination has search nor a top level destination, hide search with a circular reveal animation ends at the end button.
             */
            if (isSearchOpened)
                when (destination.id) {
                  destinationHasSearchId -> show()
                  in topLevelDestinationIds -> close()
                    else -> closeSearchWithAnimation(cXBackBtn) // It will hide not to close the search if destination is top of the destination that has search.
                }
        }
    }

    // Open the search with animation when the menuItem clicked.
    fun setupWithMenuItem(menuItem:MenuItem){
        menuItem.setOnMenuItemClickListener{
            if(!isSearchOpened)openSearchWithAnimation()
            true
        }
    }

    // Called when the query changed
    fun setOnQueryTextChangedListener(listener: (CharSequence?) -> Unit) {
        onQueryTextChangedListener = listener
    }

    fun setSearchListener(listener:AppSearchListener){
        appSearchListener=listener
    }

    // Open search view with circular reveal animation
    private fun openSearchWithAnimation() {
        preOpen() // The Search should be visible before animation
        ViewAnimationUtils.createCircularReveal(binding.root, cX, cY, 0f, revealRadius)
            .apply {
                duration = revealDuration
                addListener(object : Animator.AnimatorListener {
                  override fun onAnimationRepeat(animation: Animator?) = Unit
                  override fun onAnimationStart(animation: Animator?) = Unit
                  override fun onAnimationCancel(animation: Animator?) = Unit
                  override fun onAnimationEnd(animation: Animator?) {
                    open()
                    removeAllListeners()
                  }
                })
                start()
            }
    }

    /*
     Close search view with circular reveal animation.
     cX: the default value is this.cX and it may change when closing search with animation ends at back btn.
     */
    fun closeSearchWithAnimation(cX: Int = this.cX) {
        if(cX!=cXBackBtn)
            preClose()
        ViewAnimationUtils.createCircularReveal(binding.root, cX, cY, revealRadius, 0f)
            .apply {
                duration = revealDuration
                addListener(object : Animator.AnimatorListener {
                  override fun onAnimationRepeat(animation: Animator?) = Unit
                  override fun onAnimationStart(animation: Animator?) = Unit
                  override fun onAnimationCancel(animation: Animator?) = Unit
                  override fun onAnimationEnd(animation: Animator?) {
                    // It will hide not to close the search if destination is top of the destination that has search.
                    if (cX != cXBackBtn) close()
                    else hide()
                    removeAllListeners()
                  }
                })
                start()
            }
    }

    // Show any view
    private fun showView(v: View) {
        v.visibility = View.VISIBLE
    }

    // Hide any view
    private fun hideView(v: View) {
        v.visibility = View.INVISIBLE
    }

    // Clear Query editText
    private fun clearQuery() = binding.queryEditText.text.clear()

    companion object {
        /*
        isSearchOpened: true if the search was opened, false otherwise.
        The user might go to a destination has search. They open it and do their search. Click the result and go to the result page and back again.
        But the user expects that search wasn't closed.

        It gives more control when working with navigation architecture component.
         */
        private var isSearchOpened = false

        /*
        isSearchShown: it differs from isSearchOpened. It handles the visibility of the search view in the toolbar.
        it can be false even the isSearchOpened==true (it means it's opened in another destination but it's not showing in the toolbar).
         */
        private var isSearchShown = false
    }

    // To handle the onBackPressed()
    val shouldClose get() = isSearchShown

    // Before opening the search.
    private fun preOpen(){
        showLayout()
        post{
            appSearchListener.onSearchPreOpened()
        }
    }

    // Open the search then show it.
    private fun open() {
        isSearchOpened = true
        post{
            appSearchListener.onSearchOpened()
        }
        show()
    }

    // Show the search & keyboard.
    private fun show(){
        showLayout()
        showKeyboard()
        isSearchShown=true
        post{
            appSearchListener.onSearchShown()
        }
    }

    // Hide the search & keyboard.
    private fun hide() {
        hideLayout()
        hideKeyboard()
        isSearchShown = false
        post{
            appSearchListener.onSearchHidden()
        }
    }


    // Before closing the search.
    private fun preClose(){
        post{
            appSearchListener.onSearchPreClosed()
        }
    }

    // Hide the search & clear query then close the search.
    private fun close() {
        hide()
        clearQuery()
        isSearchOpened = false
        post{
            appSearchListener.onSearchClosed()
        }
    }

    // Show the search view
    private fun showLayout() = showView(binding.root)

    // Hide the search view
    private fun hideLayout() = hideView(binding.root)

    // Show the keyboard
    private fun showKeyboard() = showKeyboard(binding.queryEditText)

    // Hide the keyboard
    private fun hideKeyboard() = hideKeyboard(binding.queryEditText)

    interface AppSearchListener{
        fun onSearchPreOpened()
        fun onSearchOpened()
        fun onSearchShown()
        fun onSearchHidden()
        fun onSearchPreClosed()
        fun onSearchClosed()
    }
}
