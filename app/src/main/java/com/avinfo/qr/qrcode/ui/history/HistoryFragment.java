package com.avinfo.qr.qrcode.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import com.avinfo.qr.qrcode.R;
import com.avinfo.qr.qrcode.databinding.FragmentHistoryBinding;
import com.avinfo.qr.qrcode.helpers.constant.IntentKey;
import com.avinfo.qr.qrcode.helpers.itemtouch.OnStartDragListener;
import com.avinfo.qr.qrcode.helpers.itemtouch.SimpleItemTouchHelperCallback;
import com.avinfo.qr.qrcode.helpers.model.Code;
import com.avinfo.qr.qrcode.helpers.util.ProgressDialogUtil;
import com.avinfo.qr.qrcode.helpers.util.database.DatabaseUtil;
import com.avinfo.qr.qrcode.ui.base.ItemClickListener;
import com.avinfo.qr.qrcode.ui.scanresult.ScanResultActivity;

public class HistoryFragment extends Fragment implements OnStartDragListener, ItemClickListener<Code> {

    private Context mContext;
    private FragmentHistoryBinding mBinding;
    private CompositeDisposable mCompositeDisposable;
    private ItemTouchHelper mItemTouchHelper;
    private HistoryAdapter mAdapter;

    private CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    private void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mContext != null) {
            mBinding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.recyclerViewHistory.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new HistoryAdapter(this);
            mBinding.recyclerViewHistory.setAdapter(mAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(mBinding.recyclerViewHistory);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCompositeDisposable(new CompositeDisposable());

        if (mContext == null) {
            return;
        }

        ProgressDialogUtil.on().showProgressDialog(mContext);
        getCompositeDisposable().add(DatabaseUtil.on().getAllCodes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(codeList -> {
                    if (codeList.isEmpty()) {
                        mBinding.imageViewEmptyBox.setVisibility(View.VISIBLE);
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.textViewNoItemPlaceholder.setVisibility(View.GONE);
                        mBinding.imageViewEmptyBox.setVisibility(View.INVISIBLE);
                    }

                    getAdapter().clear();
                    getAdapter().addItem(codeList);
                    ProgressDialogUtil.on().hideProgressDialog();
                }, e -> ProgressDialogUtil.on().hideProgressDialog()));
    }

    private HistoryAdapter getAdapter() {
        return (HistoryAdapter) mBinding.recyclerViewHistory.getAdapter();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onItemClick(View view, Code item, int position) {
        Intent intent = new Intent(mContext, ScanResultActivity.class);
        intent.putExtra(IntentKey.MODEL, item);
        intent.putExtra(IntentKey.IS_HISTORY, true);
        startActivity(intent);
    }
}
