package app.appworks.school.stylish.ext

import androidx.fragment.app.Fragment
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.factory.ViewModelFactory

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as DiaryApplication).diaryRepository
    return ViewModelFactory(repository)
}
