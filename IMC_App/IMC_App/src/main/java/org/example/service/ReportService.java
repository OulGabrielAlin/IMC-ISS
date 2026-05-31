package org.example.service;

import org.example.domain.Credit;
import org.example.domain.Payment;
import org.example.domain.Rate;
import org.example.domain.datatypes.CreditStatus;
import org.example.repository.interfaces.CreditRepository;
import org.example.repository.interfaces.PaymentRepository;
import org.example.repository.interfaces.RateRepository;
import org.example.service.dto.FinancialReportDTO;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class ReportService {
    private CreditRepository creditRepository;
    private PaymentRepository paymentRepository;
    private RateRepository rateRepository;

    public ReportService(CreditRepository creditRepository, PaymentRepository paymentRepository, RateRepository rateRepository) {
        this.creditRepository = creditRepository;
        this.paymentRepository = paymentRepository;
        this.rateRepository = rateRepository;
    }

    public FinancialReportDTO generateFinancialReport(LocalDate startDate, LocalDate endDate)
    {
        Iterable<Credit> credits = this.creditRepository.findAll();
        Iterable<Payment> payments = this.paymentRepository.findAllInPeriod(startDate, endDate);
        Iterable<Rate> overdueRates = this.rateRepository.findAllOverdueRates(LocalDate.now());

        Integer activeCreditsCount = 0;
        Double loanAmount = 0.0;
        Double collectedAmount = 0.0;
        Integer overdueRateCount = 0;

        for (Credit credit : credits) {
            if (credit.getCreditStatus() == CreditStatus.ACTIVE) {
                activeCreditsCount++;
                loanAmount += credit.getCreditAmount();
            }
        }

        for (Payment payment : payments) {
            collectedAmount += payment.getPaymentAmount();
        }

        for (Rate rate : overdueRates) {
            overdueRateCount++;
        }

        loanAmount = Math.round(loanAmount * 100.0) / 100.0;
        collectedAmount = Math.round(collectedAmount * 100.0) / 100.0;

        return new FinancialReportDTO(activeCreditsCount, loanAmount, collectedAmount, overdueRateCount);
    }

    public void exportToCSV(FinancialReportDTO financialReportDTO, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Active Credits,Total Loan Amount,Collected Amount,Overdue Rate Count\n");

            writer.append(String.valueOf(financialReportDTO.getTotalActiveCredits())).append(",");
            writer.append(String.valueOf(financialReportDTO.getTotalLoanAmount())).append(",");
            writer.append(String.valueOf(financialReportDTO.getTotalCollectedAmount())).append(",");
            writer.append(String.valueOf(financialReportDTO.getTotalOverdueRates())).append("\n");

            writer.flush();
        }
    }
}
