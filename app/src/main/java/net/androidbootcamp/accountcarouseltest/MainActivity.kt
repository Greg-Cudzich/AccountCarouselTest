package net.androidbootcamp.accountcarouseltest

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView



class MainActivity : AppCompatActivity(){
         var institutions = mutableListOf("Chase" , "Wells Fargo")

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

        var transactions:MutableList<HashMap<String , Any>> = ArrayList()

        val transactionList = HashMap<String , Any>()
        transactionList["$5.29 Shell"] = "+$0.71 to ChangEd"
        transactionList["$3.23 Debit Card Purchase"] = "+0.77 to ChangEd"
        transactionList["$3.45 DD/BR"] = "+0.55 to ChangEd"
        transactionList["$20.00 Android"] = "+1.00 to ChangEd"
        transactionList["$20.00 DD/BR"] = "+1.00 to ChangEd"
        transactionList["$20.00 Loft"] = "+1.00 to ChangEd"
        transactionList["$20.00 Zumiez"] = "+1.00 to ChangEd"
        transactionList["$20.00 Bath and Body Works"] = "+1.00 to ChangEd"

        val transactionList2 = HashMap<String , Any>()

        transactionList2["test"] = "+$0.71 to ChangEd"
        transactionList2["test"] = "+0.77 to ChangEd"
        transactionList2["test"] = "+0.55 to ChangEd"
        transactionList2["test"] = "+1.00 to ChangEd"
        transactionList2["test"] = "+1.00 to ChangEd"
        transactionList2["test"] = "+1.00 to ChangEd"

        transactions.add(transactionList)

        transactions.add(transactionList2)
        populateList(institutions , transactions)



     }
    private fun populateList(accounts: MutableList<String>, transactions: MutableList<HashMap<String, Any>>) {
        val debts  = ArrayList<AccountItem>()
        var count = 0
        while(count < accounts.size){
                for (item in transactions[count]) {
                    val spendings = item.key
                    val roundup = item.value.toString()
                    debts.add(AccountListItem(spendings, roundup))
            }
            count++

        }

        val arrayAdapter = CustomAdapter(this@MainActivity, debts[count])
        var lv = findViewById<ListView>(R.id.institution_info)
        lv.adapter = arrayAdapter
        lv.onItemClickListener = null

        var fragments = buildFragments() as ArrayList<Fragment>
        var mViewPager = findViewById<ViewPager>(R.id.ViewPager)
        var mPageAdapter = MyFragmentPageAdapter(this, supportFragmentManager, fragments, accounts)
        mViewPager.adapter = mPageAdapter
        mPageAdapter.notifyDataSetChanged()
        mViewPager.clipToPadding = false
        mViewPager.setPadding(55, 0, 55, 0)
        mViewPager.pageMargin = 24
        mViewPager.offscreenPageLimit = 1
        mViewPager.currentItem = 0

        mViewPager.setPageTransformer(false, CarouselEffectTransformer(this)) // Set transformer
        mViewPager.pageMargin = resources.getDimensionPixelOffset(R.dimen.pager_margin)

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                var pos = position
                for(item in accounts){
                    val arrayAdapter = CustomAdapter(this@MainActivity, debts[pos])
                    var lv = findViewById<ListView>(R.id.institution_info)
                    lv.adapter = arrayAdapter
                    lv.onItemClickListener = null
                }

                Toast.makeText(this@MainActivity, institutions[position], Toast.LENGTH_LONG).show()

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }



    private fun buildFragments(): List<android.support.v4.app.Fragment> {
        val fragments = ArrayList<android.support.v4.app.Fragment>()
        for (i in 0 until institutions.size) {
            val b = Bundle()
            b.putInt("position", i)
            fragments.add(Fragment.instantiate(this, AccountCard::class.java.name, b))
        }


        return fragments
    }

    inner class MyFragmentPageAdapter(private val context: Context, fragmentManager: FragmentManager, private val myFragments: MutableList<Fragment>, private val categories: MutableList<String>) : FragmentPagerAdapter(fragmentManager) {
        var pos = 0

        override fun getItem(position: Int): Fragment {

            return myFragments[position]

        }

        override fun getCount(): Int {

            return myFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {

            pos = position
            return categories[position]
        }

        fun add(c: Class<AccountCard>, b: Bundle) {
            myFragments.add(Fragment.instantiate(context, c.name, b))
        }


    }
    inner class CarouselEffectTransformer(context: Context) : ViewPager.PageTransformer {

        private val maxTranslateOffsetX: Int
        private var viewPager: ViewPager? = null

        init {
            this.maxTranslateOffsetX = dp2px(context, 180f)
        }

        override fun transformPage(view: View, position: Float) {
            if (viewPager == null) {
                viewPager = view.parent as ViewPager?
            }

            val leftInScreen = view.left - viewPager!!.scrollX
            val centerXInViewPager = leftInScreen + view.measuredWidth / 2
            val offsetX = centerXInViewPager - viewPager!!.measuredWidth / 2
            val offsetRate = offsetX.toFloat() * 0.38f / viewPager!!.measuredWidth
            val scaleFactor = 1 - Math.abs(offsetRate)

            if (scaleFactor > 0) {
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.translationX = -maxTranslateOffsetX * offsetRate
                //ViewCompat.setElevation(view, 0.0f);
            }
            ViewCompat.setElevation(view, scaleFactor)

        }

        /**
         * Dp to pixel conversion
         */
        private fun dp2px(context: Context, dipValue: Float): Int {
            val m = context.resources.displayMetrics.density
            return (dipValue * m + 0.5f).toInt()
        }

    }

    interface AccountItem {
        val viewType: Int
        fun getView(inflater: LayoutInflater, convertView: View?): View?
    }
      class CustomAdapter(context: Context, var pos : AccountItem) : ArrayAdapter<AccountItem>(context, R.layout.institutionnames) {
         var  mInflater =  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

         enum class RowType {
             LIST_ITEM,
         }


         override fun getViewTypeCount(): Int {
             return 1
         }

         override fun getItemViewType(position: Int): Int {
             return position
         }

         override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
             return getItem(position).getView(mInflater, convertView)
         }

    }
        class AccountListItem( text1: String ,  text2 : String): AccountItem{
            private val str1: String = text1
            private val str2: String = text2

            override val viewType: Int
                get() {
                    return CustomAdapter.RowType.LIST_ITEM.ordinal
                }
        override fun getView(mInflater : LayoutInflater , convertView : View?): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.institutionnames, null)
            holder = ViewHolder()
            holder.tv1 = convertView!!.findViewById(R.id.spendings) as TextView
            holder.tv2 = convertView.findViewById(R.id.savings) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.tv1!!.text = str1
        holder.tv2!!.text = str2
        return convertView
    }
}
    class ViewHolder {
        var tv1: TextView? = null
        var tv2: TextView? = null
    }

}

