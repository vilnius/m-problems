package lt.vilnius.tvarkau.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.R;

public class ReportTypesListAdapter extends RecyclerView.Adapter<ReportTypesListAdapter.ViewHolder> {

    private List<String> reportTypes;
    private ReportTypeSelectedListener listener;
    private Context context;

    public ReportTypesListAdapter(ReportTypeSelectedListener listener, List<String> reportTypes, Context context) {
        this.reportTypes = reportTypes;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_type, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String reportType = reportTypes.get(position);

        holder.item = reportType;
        holder.reportTypeName.setText(reportType);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onReportTypeSelected(reportType);
                }
            }
        });

        if (holder.reportTypeName.getText().equals("Transporto priemonių stovėjimo tvarkos pažeidimai")) {
            holder.reportTypeName.setText(R.string.transport_vehicle_report_type_contacts_needed);
        }
    }

    public void setReportTypeSelectedListener(ReportTypeSelectedListener listener) {

        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return reportTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public String item;

        @BindView(R.id.item_report_type_name)
        public TextView reportTypeName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ReportTypeSelectedListener {

        void onReportTypeSelected(String reportType);

    }
}
