package KzeroJun.livestream.donation.repository;

import KzeroJun.livestream.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation,Long> {

}
