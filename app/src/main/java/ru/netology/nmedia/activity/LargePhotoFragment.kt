package ru.netology.nmedia.activity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.view.loadCircleCrop
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.fragment.app.viewModels
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.adapter.OnInteractionListener as OnInteractionListener


class LargePhotoFragment: Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var fragmentBinding: FragmentLargePhotoBinding? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLargePhotoBinding.inflate(
            inflater,
            container,
            false
        )

        val post = Post(
            id = 0,
            content = "",
            author = "",
            authorAvatar = "",
            likedByMe = false,
            likes =    0,
            published = "",
            newer = 0,

            attachment = Attachment(
                url = "http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png",
                type = AttachmentType.IMAGE
            )
        )

        fragmentBinding = binding

        binding.apply {
            like.isChecked = post.likedByMe
            like.text =arguments?.getString("likes")

            photo.isVisible = post.attachment != null
            post.attachment?.let {

                Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")

                Glide.with(photo)
                    //  .load(arguments?.textArg)
                    .load(  arguments?.getString("url"))

                    .timeout(10_000)
                    .into(photo)

                like.setOnClickListener {

                    if (post.likedByMe) {
                        viewModel.unlikeById(post.id)
                    } else {
                        viewModel.likeById(post.id)

                    }

                    binding.share.setOnClickListener {

                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.content)
                            type = "text/plain"
                        }
                    }
                }

            }

            return binding.root
        }
    }
}


















//
//
//
//    companion object {
//        var Bundle.textArg: String? by StringArg
//    }
//
//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )
//    private var fragmentBinding: FragmentLargePhotoBinding? = null
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val binding = FragmentLargePhotoBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//
//        fragmentBinding = binding
//
//        arguments?.textArg
//            ?.let(binding.like::setText)
//
//        arguments?.textArg
//            ?.let(binding.share::setText)
//
//
//        val apply = binding.apply {
//            fun bind(post: Post): ConstraintLayout {
//                like.text = "${binding.like.text.toString()}"
//                like.isChecked = post.likedByMe
//
//
//                photo.isVisible = post.attachment != null
//                post.attachment?.let {
//                    Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")
//
//                    Glide.with(photo)
//                        //  .load("${BuildConfig.BASE_URL}/media/${it.url}")
//                        .load("${it.url.toString()}")
//                        .timeout(10_000)
//                        .into(photo)
//
//                    like.setOnClickListener {
////                        fun onLike(post: Post) {
////                            if (post.likedByMe) {
////                                viewModel.unlikeById(post.id)
////                            } else {
////                                viewModel.likeById(post.id)
////                            }
////
////                        }
//                    }
//                    binding.share.setOnClickListener {
////                            fun onShare(post: Post) {
////                                val intent = Intent().apply {
////                                    action = Intent.ACTION_SEND
////                                    putExtra(Intent.EXTRA_TEXT, post.content)
////                                    type = "text/plain"
////                                }
////
////                                val shareIntent =
////                                    Intent.createChooser(
////                                        intent,
////                                        getString(R.string.chooser_share_post)
////                                    )
////                                startActivity(shareIntent)
//                            }
//}
//                    }
//    }
//}
//                    return binding.root
//
//                }





//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment,
//    )
//
//    private var fragmentBinding: FragmentLargePhotoBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentLargePhotoBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//
//        fragmentBinding = binding
//
//        arguments?.textArg
//            ?.let(binding.like::setText,)
//
//        arguments?.textArg
//            ?.let(binding.share::setText,)
//
//
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        fragmentBinding = null
//        super.onDestroyView()
//    }
//}
//











//
//    private val viewModel: PostViewModel by viewModels(
//        ownerProducer = ::requireParentFragment,
//    )
//
//    private var fragmentBinding: FragmentLargePhotoBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentLargePhotoBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//        arguments?.textArg
//            ?.let(binding.like::setText,)
//
//        binding.apply {
//            like.setOnClickListener {
//
//            }
//        }
//
//        arguments?.textArg
//            ?.let(binding.share::setText,)
//
//        binding.photo.setImageURI(Uri.parse("${BuildConfig.BASE_URL}/attachment/моя_картинка.jpg"))
//
//
//        binding.photo.isVisible =i.attachment != null
//            .attachment?.let {
//                Glide.with(photo)
//                    .load("${BuildConfig.BASE_URL}/media/${it.url}")
//                    .timeout(10_000)
//                    .into(photo)
//            }
//
//
//
//
//
//
//
//
//        return binding.root
//


//
//
//
//
//
//
//
//
//    }
//
//    override fun onDestroyView() {
//        fragmentBinding = null
//        super.onDestroyView()
//    }
//}
//



